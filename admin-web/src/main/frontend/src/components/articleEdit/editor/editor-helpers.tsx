import EnvUtils from "../../../utils/env-utils";

export const getBorder = () => {
    return EnvUtils.isDarkMode() ? `1px solid rgba(253, 253, 253, 0.12)` : "1px solid #DDD"
}