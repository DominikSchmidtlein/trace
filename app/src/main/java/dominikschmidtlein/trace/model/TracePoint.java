package dominikschmidtlein.trace.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by domin_2o9sb4z on 2016-11-23.
 */
public class TracePoint {

    double x;
    double y;

    private List<Connection> connections = new ArrayList<>();

    public TracePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void addConnection(Connection connection) {
        connections.add(connection);
    }

    public Connection connectedTo(TracePoint tracePoint) {
        return null;
    }

}
