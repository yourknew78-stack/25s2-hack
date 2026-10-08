package reactions;

import dao.model.Message;

/** Produces front-end tags according to a reaction summarization algorithm. */
public interface IReactionReporter {
    /**
     * Summarizes the current reactions on a message.
     *
     * @param message the message to summarize
     * @return the algorithm's ordered display tags
     */
    public ReactionDisplayTag[] generateReport(Message message);
}
