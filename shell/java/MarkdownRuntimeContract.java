import java.lang.reflect.Method;

public final class MarkdownRuntimeContract {

    private MarkdownRuntimeContract() {
    }

    public static void main(String[] args) throws Exception {
        Class<?> rendererClass = Class.forName(
                "com.zrlog.blog.polyglot.markdown.MarkdownJsRenderer");
        Object renderer = rendererClass.getDeclaredConstructor().newInstance();

        if (Runtime.version().feature() < 17) {
            return;
        }

        Method render = rendererClass.getMethod("render", String.class);
        Object html = render.invoke(renderer, "# Packaging contract");
        if (!(html instanceof String)
                || !((String) html).contains("<h1>Packaging contract</h1>")) {
            throw new AssertionError("Packaged Markdown renderer did not execute through GraalJS");
        }
    }
}
