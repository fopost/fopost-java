import com.fopost.sdk.FoPost;
import com.fopost.sdk.model.Account;
import com.fopost.sdk.model.Post;
import com.fopost.sdk.model.Workspace;
import com.fopost.sdk.param.CreatePostParams;
import java.util.List;

/**
 * Creates a post against a running API, and optionally publishes it.
 *
 * <pre>
 * export FOPOST_API_KEY=fp_...
 * export FOPOST_BASE_URL=http://localhost:8080     # optional
 *
 * mvn -q dependency:build-classpath -Dmdep.outputFile=/tmp/cp.txt
 * mvn -q package -DskipTests
 * java -cp "target/classes:$(cat /tmp/cp.txt)" examples/CreatePost.java "Hello from Java" --publish
 * </pre>
 */
public final class CreatePost {

    public static void main(String[] args) {
        String text = args.length > 0 ? args[0] : "Hello from the Java SDK";
        boolean publish = List.of(args).contains("--publish");

        FoPost.Builder builder = FoPost.builder();
        String baseUrl = System.getenv("FOPOST_BASE_URL");
        if (baseUrl != null) {
            builder.baseUrl(baseUrl);
        }
        FoPost client = builder.build();

        List<Workspace> workspaces = client.workspaces().list();
        if (workspaces.isEmpty()) {
            System.out.println("No workspaces on this key.");
            return;
        }
        Workspace workspace = workspaces.get(0);

        List<Account> accounts = client.accounts().list(workspace.id());
        if (accounts.isEmpty()) {
            System.out.println("No connected accounts in " + workspace.name() + ".");
            return;
        }

        Post post = client.posts().create(CreatePostParams.of(workspace.id())
                .content(text)
                .accounts(accounts.stream().map(Account::id).toList()));

        System.out.println("Created " + post.id() + " (" + post.status() + ")");

        if (publish) {
            var result = client.posts().publish(post.id());
            System.out.println("Publishing: " + result.postStatus());
            result.deliveries().forEach(d -> System.out.println("  " + d.platform() + " → " + d.status()));
        }
    }
}
