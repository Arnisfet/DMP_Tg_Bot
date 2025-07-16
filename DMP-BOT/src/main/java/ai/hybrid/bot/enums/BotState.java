package ai.hybrid.bot.enums;

public enum BotState {
    INIT,
    MAIN_MENU,

    // Launch flow
    ACTION,
    JOB,
    CLUSTER,

    // Health Check flow
    HEALTH_INIT,
    HEALTH_OPTION,
    HEALTH_RESULT;
    public BotState next() {
        return switch (this) {
            case INIT -> BotState.MAIN_MENU;
            case MAIN_MENU -> BotState.ACTION;
            case ACTION -> BotState.JOB;
            case JOB -> BotState.CLUSTER;
            case CLUSTER -> BotState.INIT;


            case HEALTH_INIT -> null;
            case HEALTH_OPTION -> null;
            case HEALTH_RESULT -> null;
        };
    }
}
