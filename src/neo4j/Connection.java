package neo4j;

import org.neo4j.driver.v1.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayomitundefafore on 9/2/17.
 */
public class Connection {
    static Driver driver;
    static Session session;

    static {
        driver = GraphDatabase.driver("bolt://127.0.0.1:7687", AuthTokens.basic("neo4j", "ayomiFafore1"));
        session = driver.session();
    }

    public static void create(String stmt) {
        System.out.println(stmt);
        session.run(stmt);
    }

    public static void cleanUp() {
        session.run("Match (n) detach delete n");
    }
    public static List<Record> getRecord(String query) {
        List<Record> records = new ArrayList<>();
        StatementResult result = session.run(query);
        while (result.hasNext())
            records.add(result.next());
        return records;
    }
}
