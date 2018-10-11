package odms.controller.gui.widget;

import javafx.beans.NamedArg;
import javafx.collections.ObservableList;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class ColoredLineChart<X, Y extends Number> extends LineChart<X, Y> {

    private Polygon colorPoly;

    public ColoredLineChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
        super(xAxis, yAxis);
    }

    public ColoredLineChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X, Y>> data) {
        super(xAxis, yAxis, data);
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        if (!(getData().size() < 3)) {
            Series lowerBound = getData().get(1);
            Series upperBound = getData().get(2);
            ObservableList<Data<X, Y>> lower = lowerBound.getData();
            ObservableList<Data<X, Y>> upper = upperBound.getData();

            for (int i = 0; i < lower.size() - 1; i++) {
                double x1 = getXAxis().getDisplayPosition(upper.get(i).getXValue());
                double y1 = getYAxis().getDisplayPosition(lower.get(i).getYValue());
                double x2 = getXAxis().getDisplayPosition(upper.get((i + 1)).getXValue());
                double y2 = getYAxis().getDisplayPosition(lower.get(i + 1).getYValue());
                getPlotChildren().remove(colorPoly);
                colorPoly = new Polygon();
                Color linearGrad = Color.rgb(0, 255, 0, 0.1);

                colorPoly.getPoints().addAll(x1, y1,
                        x1, getYAxis().getDisplayPosition(upper.get(i).getYValue()),
                        x2, getYAxis().getDisplayPosition(upper.get((i + 1)).getYValue()),
                        x2, y2);
                getPlotChildren().add(colorPoly);
                colorPoly.toFront();
                colorPoly.setFill(linearGrad);
            }
        } else {
            getPlotChildren().remove(colorPoly);
        }
    }
}
