package demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class SWTDemo {

    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Demo");

//        createMessageBox(shell);
//        createSpinner(shell);
//        createTree(shell);
//        createStyleDemo(shell);
//        createFormLayout(shell);
//        createForm(shell);

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    public static void createMessageBox(Shell shell) {
        MessageBox mb = new MessageBox(shell, SWT.ICON_WARNING);
        mb.setText("Text Box");
        mb.setMessage("Hello World");
        mb.open();
    }

    private static void createSpinner(Composite composite) {
        composite.setLayout(new GridLayout(1, true));
        DateTime dateTime = new DateTime(composite, SWT.TIME);
        Spinner spinner = new Spinner(composite, SWT.NONE);
        spinner.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent var1) {
                System.out.println(spinner.getSelection());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent var1) {
            }
        });
        spinner.setValues(5, 0, 10, 0, 1, 1);
    }

    private static void createTree(Composite composite) {
        // Set the single-selection tree in the upper left,
        // the multi-selection tree in the upper right,
        // and the checkbox tree across the bottom.
        // To do this, create a 1x2 grid, and in the top
        // cell, a 2x1 grid.
        composite.setLayout(new GridLayout(1, true));
        Composite top = new Composite(composite, SWT.NONE);
        GridData data = new GridData(GridData.FILL_BOTH);
        top.setLayoutData(data);

        top.setLayout(new GridLayout(2, true));
        Tree single = new Tree(top, SWT.SINGLE | SWT.BORDER);
        data = new GridData(GridData.FILL_BOTH);
        single.setLayoutData(data);
        fillTree(single);

        Tree multi = new Tree(top, SWT.MULTI | SWT.BORDER);
        data = new GridData(GridData.FILL_BOTH);
        multi.setLayoutData(data);
        fillTree(multi);

        Tree check = new Tree(composite, SWT.CHECK | SWT.BORDER);
        data = new GridData(GridData.FILL_BOTH);
        check.setLayoutData(data);
        fillTree(check);
    }

    /**
     * Helper method to fill a tree with data
     *
     * @param tree the tree to fill
     */
    private static void fillTree(Tree tree) {
        // Turn off drawing to avoid flicker
        tree.setRedraw(false);

        // Create five root items
        for (int i = 0; i < 5; i++) {
            TreeItem item = new TreeItem(tree, SWT.NONE);
            item.setText("Root Item " + i);

            // Create three children below the root
            for (int j = 0; j < 3; j++) {
                TreeItem child = new TreeItem(item, SWT.NONE);
                child.setText("Child Item " + i + " - " + j);

                // Create three grandchildren under the child
                for (int k = 0; k < 3; k++) {
                    TreeItem grandChild = new TreeItem(child, SWT.NONE);
                    grandChild.setText("Grandchild Item " + i + " - " + j + " - " + k);
                }
            }
        }
        // Turn drawing back on!
        tree.setRedraw(true);
    }

    public static void createStyleDemo(Composite composite) {
        // There are nine distinct styles, so create
        // a 3x3 grid
        composite.setLayout(new GridLayout(3, true));

        // The SWT.BORDER style
        Decorations d = new Decorations(composite, SWT.BORDER);
        d.setLayoutData(new GridData(GridData.FILL_BOTH));
        d.setLayout(new FillLayout());
        new Label(d, SWT.CENTER).setText("SWT.BORDER");

        // The SWT.CLOSE style
        d = new Decorations(composite, SWT.CLOSE);
        d.setLayoutData(new GridData(GridData.FILL_BOTH));
        d.setLayout(new FillLayout());
        new Label(d, SWT.CENTER).setText("SWT.CLOSE");

        // The SWT.MIN style
        d = new Decorations(composite, SWT.MIN);
        d.setLayoutData(new GridData(GridData.FILL_BOTH));
        d.setLayout(new FillLayout());
        new Label(d, SWT.CENTER).setText("SWT.MIN");

        // The SWT.MAX style
        d = new Decorations(composite, SWT.MAX);
        d.setLayoutData(new GridData(GridData.FILL_BOTH));
        d.setLayout(new FillLayout());
        new Label(d, SWT.CENTER).setText("SWT.MAX");

        // The SWT.NO_TRIM style
        d = new Decorations(composite, SWT.NO_TRIM);
        d.setLayoutData(new GridData(GridData.FILL_BOTH));
        d.setLayout(new FillLayout());
        new Label(d, SWT.CENTER).setText("SWT.NO_TRIM");

        // The SWT.RESIZE style
        d = new Decorations(composite, SWT.RESIZE);
        d.setLayoutData(new GridData(GridData.FILL_BOTH));
        d.setLayout(new FillLayout());
        new Label(d, SWT.CENTER).setText("SWT.RESIZE");

        // The SWT.TITLE style
        d = new Decorations(composite, SWT.TITLE);
        d.setLayoutData(new GridData(GridData.FILL_BOTH));
        d.setLayout(new FillLayout());
        new Label(d, SWT.CENTER).setText("SWT.TITLE");
        // The SWT.ON_TOP style
        d = new Decorations(composite, SWT.ON_TOP);
        d.setLayoutData(new GridData(GridData.FILL_BOTH));
        d.setLayout(new FillLayout());
        new Label(d, SWT.CENTER).setText("SWT.ON_TOP");

        // The SWT.TOOL style
        d = new Decorations(composite, SWT.TOOL);
        d.setLayoutData(new GridData(GridData.FILL_BOTH));
        d.setLayout(new FillLayout());
        new Label(d, SWT.CENTER).setText("SWT.TOOL");
    }

    public static void createFormLayout(Shell shell) {
        FormLayout layout = new FormLayout();
        shell.setLayout(layout);
        Button one = new Button(shell, SWT.PUSH);
        one.setText("One");
        FormData data = new FormData();
        data.top = new FormAttachment(0, 5);
        data.left = new FormAttachment(0, 5);
        data.bottom = new FormAttachment(50, -5);
        data.right = new FormAttachment(50, -5);
        one.setLayoutData(data);

        Composite composite = new Composite(shell, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        composite.setLayout(gridLayout);
        Button two = new Button(composite, SWT.PUSH);
        two.setText("two");
        GridData gridData = new GridData(GridData.FILL_BOTH);
        two.setLayoutData(gridData);
        Button three = new Button(composite, SWT.PUSH);
        three.setText("three");
        gridData = new GridData(GridData.FILL_BOTH);
        three.setLayoutData(gridData);
        Button four = new Button(composite, SWT.PUSH);
        four.setText("four");
        gridData = new GridData(GridData.FILL_BOTH);
        four.setLayoutData(gridData);
        data = new FormData();
        data.top = new FormAttachment(0, 5);
        data.left = new FormAttachment(one, 5);
        data.bottom = new FormAttachment(50, -5);
        data.right = new FormAttachment(100, -5);
        composite.setLayoutData(data);

        Button five = new Button(shell, SWT.PUSH);
        five.setText("five");
        data = new FormData();
        data.top = new FormAttachment(one, 5);
        data.left = new FormAttachment(0, 5);
        data.bottom = new FormAttachment(100, -5);
        data.right = new FormAttachment(100, -5);
        five.setLayoutData(data);
        shell.pack();
    }

    public static void createForm(Shell shell) {
        GridLayout layout = new GridLayout(2, false);
        shell.setLayout(layout);
        GridData data;

        data = new GridData();
        data.verticalAlignment = SWT.CENTER;
        Label label2 = new Label(shell, SWT.RIGHT);
        label2.setText("Name");
        label2.setLayoutData(data);

        Text txt2 = new Text(shell, SWT.BORDER);
        data = new GridData();
        txt2.setLayoutData(data);

        data = new GridData();
        data.verticalAlignment = SWT.BEGINNING;
        Label label = new Label(shell, SWT.RIGHT);
        label.setText("Description");
        label.setLayoutData(data);

        final Label ll = new Label(shell, SWT.RIGHT);
        ll.setText("My description");

        final Text txt = new Text(shell, SWT.BORDER | SWT.MULTI |
                SWT.V_SCROLL | SWT.WRAP);
        data = new GridData();
        data.heightHint = 100;
        txt.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent arg0) {
                int chars = txt.getCharCount();
                ll.setText(String.valueOf(100 - chars));
            }
        });
        txt.setLayoutData(data);

        data = new GridData();
        data.verticalAlignment = SWT.CENTER;
        Label label3 = new Label(shell, SWT.RIGHT);
        label3.setText("Colour");
        label3.setLayoutData(data);

        Color colour = new Color(null, 123, 234, 12);
//        Label txt3 = new Label(parent, SWT.NONE);
//        Button txt3 = new Button(parent, SWT.PUSH);
        Text txt3 = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
        txt3.setBackground(colour);
        data = new GridData();
        data.widthHint = 100;
        txt3.setLayoutData(data);

        data = new GridData();
        data.verticalAlignment = SWT.CENTER;
        Label label4 = new Label(shell, SWT.RIGHT);
        label4.setText("Font");
        label4.setLayoutData(data);

        Button button = new Button(shell, SWT.PUSH);
        button.setText("Font...");
        button.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent selectionevent) {
                FontDialog fd = new FontDialog(shell, SWT.NONE);
                fd.setText("Select font");
                FontData defaultFont = new FontData("Times New Roman", 12,
                        SWT.NORMAL);
                FontData[] fontList = {defaultFont};
                fd.setFontList(fontList);
                fd.setRGB(new RGB(0, 0, 0));
                fd.open();
            }

            public void widgetDefaultSelected(SelectionEvent selectionevent) {
                // TODO Auto-generated method stub
            }
        });
    }
}

