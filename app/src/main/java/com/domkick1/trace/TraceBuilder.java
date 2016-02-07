package com.domkick1.trace;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by domin_2o9sb4z on 2016-01-30.
 */
public class TraceBuilder extends Trace {

    private Mode mode;
    private ArrayList<Point> points;
    private ArrayList<Line> shape;


    public TraceBuilder(android.graphics.Point screenSize, int actionBarHeight, Mode mode) {
        super(screenSize, actionBarHeight);
        this.mode = mode;
        points = Levels.getGridAsPoints(mode, screenSize.x, screenSize.y, actionBarHeight);
        shape = new ArrayList<>(50);
    }

    public enum Mode {SQUARE, ISOMETRIC}


    /**
     * Checks if there's a point in the vicinity. Update most recent line if not. Otherwise add new
     * line to shape array and start new line.
     * If user lifted finger, remove unfinished line.
     *
     * @param event
     * @return
     */
    @Override
    protected boolean handleTouch(View v, MotionEvent event) {
        if (event.getPointerCount() > 1)
            return false;

        Point touchPoint = new Point(event.getX(), event.getY());
        Point nearPoint = isNearVertexInPoints(touchPoint, points, RADIUS);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (nearPoint != null)
                    shape.add(new Line(nearPoint, touchPoint));
                return nearPoint != null;

            case MotionEvent.ACTION_MOVE:
                if (nearPoint != null) {
                    if (!shape.get(shape.size() - 1).getP1().equals(nearPoint)) {
                        Line newLine = new Line(shape.get(shape.size() - 1).getP1(), nearPoint);
                        shape.remove(shape.size() - 1);

                        ArrayList<Line> simp = getSimpleLines(newLine);
                        shape.addAll(simp);

                        shape.add(new Line(nearPoint, touchPoint));
                        return true;
                    }
                }
                replaceLastPoint(touchPoint, shape);
                return true;
            default:
                shape.remove(shape.size() - 1);
                return false;

        }
    }

    protected ArrayList<Line> getSimpleLines(Line line) {
        ArrayList<Point> intersectingPoints = getIntersectingPoints(line);
        sortPoints(intersectingPoints, line);
        ArrayList<Line> simpleLines = new ArrayList<>(intersectingPoints.size() - 1);

        for (int i = 0; i < intersectingPoints.size() - 1; i++)
            simpleLines.add(new Line(intersectingPoints.get(i), intersectingPoints.get(i + 1)));
        return simpleLines;
    }

    protected ArrayList<Point> getIntersectingPoints(Line line) {
        ArrayList<Point> intersectingPoints = new ArrayList<>();
        for (Point point : points)
            if (line.intersects(point))
                intersectingPoints.add(point);
        return intersectingPoints;
    }

    protected void sortPoints(ArrayList<Point> points, Line line) {
        boolean swapped;
        do {
            swapped = false;
            for (int i = 1; i < points.size(); i++) {
                if (line.getP1().getDistance(points.get(i - 1)) > line.getP1().getDistance(points.get(i))) {
                    swap(i - 1, i, points);
                    swapped = true;
                }
            }
        } while (swapped);
        if (!line.getP2().equals(points.get(points.size() - 1)))
            throw new UnsupportedOperationException();
    }

    protected void swap(int i1, int i2, ArrayList<Point> points) {
        Point temp = points.get(i1);
        points.set(i1, points.get(i2));
        points.set(i2, temp);
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public ArrayList<Line> getShape() {
        return shape;
    }
}
