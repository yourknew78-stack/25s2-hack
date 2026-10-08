package dao;

import dao.model.Reaction;
import reactions.ReactionType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/** Bounded summaries of current reactions on one message, maintained by ReactionDAO. */
final class ReactionReportIndex {
    private static final int LIMIT = 5;
    private static final Comparator<Reaction> BY_TIME = Comparator.comparingLong(Reaction::timestamp);

    private final int[] counts = new int[ReactionType.values().length];
    private final Reaction[] earliest = new Reaction[counts.length];
    private final List<Reaction> oldest = new ArrayList<>(LIMIT);
    private final List<Reaction> overview = new ArrayList<>(LIMIT);

    void add(Reaction reaction) {
        int type = reaction.type().ordinal();
        counts[type]++;
        if (earliest[type] == null || reaction.timestamp() < earliest[type].timestamp()) {
            earliest[type] = reaction;
        }
        considerOldest(reaction);
        rankOverview();
    }

    void remove(Reaction reaction, Collection<Reaction> remaining) {
        int type = reaction.type().ordinal();
        counts[type]--;
        boolean replaceEarliest = reaction == earliest[type];
        boolean replaceOldest = oldest.contains(reaction);
        if (replaceEarliest) earliest[type] = null;
        if (replaceOldest) oldest.clear();

        // Task 1 already removes from a message list in O(n). Refill only when a
        // summary loses its candidate, reusing that list rather than another store.
        if (replaceEarliest || replaceOldest) {
            for (Reaction current : remaining) {
                if (replaceOldest) considerOldest(current);
                if (replaceEarliest && current.type() == reaction.type()
                        && (earliest[type] == null || current.timestamp() < earliest[type].timestamp())) {
                    earliest[type] = current;
                }
            }
        }
        rankOverview();
    }

    private void considerOldest(Reaction reaction) {
        for (int i = 0; i < oldest.size(); i++) {
            Reaction candidate = oldest.get(i);
            if (candidate.userUUID().equals(reaction.userUUID())) {
                if (reaction.timestamp() < candidate.timestamp()) {
                    oldest.set(i, reaction);
                    oldest.sort(BY_TIME);
                }
                return;
            }
        }
        if (oldest.size() < LIMIT) {
            oldest.add(reaction);
        } else if (reaction.timestamp() < oldest.get(LIMIT - 1).timestamp()) {
            oldest.set(LIMIT - 1, reaction);
        } else {
            return;
        }
        oldest.sort(BY_TIME);
    }

    private void rankOverview() {
        overview.clear();
        for (Reaction reaction : earliest) {
            if (reaction == null) continue;
            int position = 0;
            while (position < overview.size() && !precedes(reaction, overview.get(position))) {
                position++;
            }
            if (position < LIMIT) {
                overview.add(position, reaction);
                if (overview.size() > LIMIT) overview.remove(LIMIT);
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
