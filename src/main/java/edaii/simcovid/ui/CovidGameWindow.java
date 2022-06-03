package edaii.simcovid.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;


public class CovidGameWindow {

    final Frame root;
    final JPanel panel;
    GridLayout gridLayout = new GridLayout(3, 3);
    Cell[] cells;
    int lastId = 0;

    public synchronized void setCellStates(List<Integer> cellStates) {
        if (cellStates.size() != gridSize())
            throw new IllegalArgumentException("Provided cell states does not match with grid size");

        int i = 0;
        for (Integer newState : cellStates) {
            cells[i].setState(newState);
            i++;
        }
    }

    class CovidWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }

    public CovidGameWindow() {
        root = new Frame();
        root.setTitle("Sim Covid");
        root.setSize(800, 600);
        root.setLocationRelativeTo(null);

        panel = new JPanel();
        root.add(panel);

        setRowsAndColumns(3, 3);

        root.addWindowListener(new CovidWindowListener());
        root.setVisible(true);
    }

    public void setRowsAndColumns(int rows, int columns) {
        panel.removeAll();
        fillWithLabels(rows * columns);
        gridLayout = new GridLayout(rows, columns);
        panel.setLayout(gridLayout);
        panel.revalidate();
    }

    public int gridSize() {
        return gridLayout.getColumns() * gridLayout.getRows();
    }

    private void fillWithLabels(int count) {
        final int elementsToAdd = count;
        cells = new Cell[elementsToAdd];
        for (int i = 0; i < elementsToAdd; ++i) {
            var newCell = new Cell(lastId++);
            cells[i] = newCell;
            panel.add(newCell);
        }
    }
}
