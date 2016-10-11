package demo;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

class CassandraDemo {

    private Cluster cluster;
    private Session session;

    public static void main(String[] args) {
        CassandraDemo client = new CassandraDemo();
        client.connect("127.0.0.1");
        try {
            client.querySchema();
        } finally {
            client.close();
        }
    }

    public void connect(String node) {
        cluster = Cluster.builder().addContactPoint(node).build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
                    host.getDatacenter(), host.getAddress(), host.getRack());
        }
        session = cluster.connect("public");
    }

    public void querySchema() {
        ResultSet results = session.execute("SELECT * FROM users WHERE user_id = 1745;");
        for (Row row : results) {
            for (int i = 0; i < row.getColumnDefinitions().size(); i++) {
                String value = row.getObject(i).toString();
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    public void close() {
        session.close();
        cluster.close();
    }

    public Session getSession() {
        return session;
    }

}
