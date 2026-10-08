package dao;

import dao.model.Reaction;
import reactions.ReactionType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Incremental Task 2 summaries for one message, maintained by ReactionDAO.
 * Counts cover every current reaction; report candidates are bounded to five.
 * Oldest candidates represent distinct users. Overview candidates represent
 * distinct types, ranked by descending count then earliest current timestamp.
 * Reads return copies so callers cannot change the maintained summaries.
 */
final class ReactionReportIndex {
    private static final int MAX_REPORT_REACTIONS = 5;
    private static final Comparator<Reaction> BY_TIMESTAMP = Comparator.comparingLong(Reaction::timestamp);

    private final int[] counts = new int[ReactionType.values().length];
    private final Reaction[] earliest = new Reaction[counts.length];
    private final List<Reaction> oldest = new ArrayList<>(MAX_REPORT_REACTIONS);
    private final List<Reaction> overview = new ArrayList<>(MAX_REPORT_REACTIONS);

    /** Called once per newly stored reaction; duplicate identities are rejected by the DAO. */
    void add(Reaction reaction) {
        int typeIndex = reaction.type().ordinal();
        counts[typeIndex]++;
        if (earliest[typeIndex] == null || reaction.timestamp() < earliest[typeIndex].timestamp()) {
            earliest[typeIndex] = reaction;
        }
        considerOldest(reaction);
        rankOverview();
    }

    /**
     * Removes a stored reaction after the DAO has removed it from the message list.
     * The supplied collection contains exactly the remaining reactions; cached
     * counts and candidates are restored before this method returns.
     */
    void remove(Reaction reaction, Collection<Reaction> remaining) {
        int typeIndex = reaction.type().ordinal();
        counts[typeIndex]--;
        boolean replaceEarliest = reaction == earliest[typeIndex];
        boolean replaceOldest = oldest.contains(reaction);
        if (replaceEarliest) {
            earliest[typeIndex] = null;
        }
        if (replaceOldest) {
            oldest.clear();
        }

        if (replaceEarliest || replaceOldest) {
            rebuildRemovedCandidates(reaction.type(), remaining, replaceEarliest, replaceOldest);
        }
        rankOverview();
    }

    /** Reuses the remaining message data only when a cached candidate was removed. */
    private void rebuildRemovedCandidates(ReactionType removedType,
                                          Collection<Reaction> remaining,
                                          boolean replaceEarliest, boolean replaceOldest) {
        int typeIndex = removedType.ordinal();
        for (Reaction current : remaining) {
            if (replaceOldest) {
                considerOldest(current);
            }
            if (replaceEarliest && current.type() == removedType
                    && (earliest[typeIndex] == null
                    || current.timestamp() < earliest[typeIndex].timestamp())) {
                earliest[typeIndex] = current;
            }
        }
    }

    private void considerOldest(Reaction reaction) {
        for (int i = 0; i < oldest.size(); i++) {
            Reaction candidate = oldest.get(i);
            if (candidate.userUUID().equals(reaction.userUUID())) {
                if (reaction.timestamp() < candidate.timestamp()) {
                    oldest.set(i, reaction);
                    oldest.sort(BY_TIMESTAMP);
                }
                return;
            }
        }
        if (oldest.size() < MAX_REPORT_REACTIONS) {
            oldest.add(reaction);
        } else if (reaction.timestamp() < oldest.get(MAX_REPORT_REACTIONS - 1).timestamp()) {
            oldest.set(MAX_REPORT_REACTIONS - 1, reaction);
        } else {
            return;
        }
        oldest.sort(BY_TIMESTAMP);
    }

    private void rankOverview() {
        overview.clear();
        for (Reaction reaction : earliest) {
            if (reaction == null) {
                continue;
            }
            int position = 0;
            while (position < overview.size() && !precedes(reaction, overview.get(position))) {
                position++;
            }
            if (position < MAX_REPORT_REACTIONS) {
                overview.add(position, reaction);
                if (overview.size() > MAX_REPORT_REACTIONS) {
                    overview.remove(MAX_REPORT_REACTIONS);
                }
            }
        }
    }

    private boolean precedes(Reaction left, Reaction right) {
        int leftCount = counts[left.type().ordinal()];
        int rightCount = counts[right.type().ordinal()];
        return leftCount > rightCount
                || (leftCount == rightCount && left.timestamp() < right.timestamp());
    }

    List<Reaction> oldest() {
        return new ArrayList<>(oldest);
    }

    List<Reaction> overview() {
        return new ArrayList<>(overview);
    }

    int count(ReactionType type) {
        return counts[type.ordinal()];
    }
}
