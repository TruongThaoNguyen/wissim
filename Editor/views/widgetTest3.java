package views;

/*******************************************************************************
 * Copyright (c) 2004 Berthold Daum. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Common
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: Berthold Daum
 ******************************************************************************/

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class widgetTest3 {

  public static void main(String[] args) {
    // Create display instance
    final Display display = new Display();
    // Create top level shell (pass display as parent)
    final Shell toplevelShell = new Shell(display);
    // Set title
    toplevelShell.setText("TopLevel.Titelzeile");
    // Fill the shell completely with content
    toplevelShell.setLayout(new FillLayout());
    // Create tabbed folder
    TabFolder folder = new TabFolder(toplevelShell, SWT.NONE);
    // Protocol selection event
    folder.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        System.out.println("Tab selected: "
            + ((TabItem) (e.item)).getText());
      }
    });
    // Fill tabbed folder completely with content
    folder.setLayout(new FillLayout());
    Composite page1 = createTabPage(folder, "tab1");
    // We can now place more GUI elements onto page1
    //...
    Composite page2 = createTabPage(folder, "tab2");
    // We can now place more GUI elements onto page2
    //...
    // Display shell
    toplevelShell.open();
    // Event loop
    while (!toplevelShell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
  }

  private static Composite createTabPage(TabFolder folder, String label) {
    // Create and label a new tab
    TabItem tab = new TabItem(folder, SWT.NONE);
    tab.setText(label);
    // Create a new page as a Composite instance
    Composite page = new Composite(folder, SWT.NONE);
    //... and assign to tab
    tab.setControl(page);
    return page;
  }

  /** * Menu ** */
  public static Menu createMenu(final Shell toplevelShell) {
    //     Menuleiste anlegen
    Menu menuBar = new Menu(toplevelShell, SWT.BAR);
    toplevelShell.setMenuBar(menuBar);
    //     Menutitel anlegen
    MenuItem fileTitle = new MenuItem(menuBar, SWT.CASCADE);
    fileTitle.setText("File");
    //     Untermenu fur diesen Menutitel anlegen
    Menu fileMenu = new Menu(toplevelShell, SWT.DROP_DOWN);
    fileTitle.setMenu(fileMenu);
    //     Menueintrag anlegen
    MenuItem item = new MenuItem(fileMenu, SWT.NULL);
    item.setText("Exit");
    //     Ereignisverarbeitung fur Menueintrag
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        toplevelShell.close();
      }
    });
    return menuBar;
  }

  /** * Coolbar ** */

  public CoolBar createCoolBar(Composite composite) {
    //     Create CoolBar
    final CoolBar coolbar = new CoolBar(composite, SWT.NULL);
    //     Create ToolBar as a component of CoolBar
    final ToolBar toolbar1 = new ToolBar(coolbar, SWT.NULL);
    //     Create pushbutton
    final ToolItem toolitem1 = new ToolItem(toolbar1, SWT.PUSH);
    toolitem1.setText("Push");
    toolitem1.setToolTipText("Push button");
    //     Create event processing for pushbutton
    toolitem1.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        System.out.println("Tool button was pressed: "
            + toolitem1.getText());
      }
    });
    //     Create check button
    final ToolItem toolitem2 = new ToolItem(toolbar1, SWT.CHECK);
    toolitem2.setText("Check");
    toolitem2.setToolTipText("Check button");
    //     Create CoolItem instance
    final CoolItem coolitem1 = new CoolItem(coolbar, SWT.NULL);
    //     Assign this tool bar to the CoolItem instance
    coolitem1.setControl(toolbar1);
    //     Compute size of tool bar
    Point size = toolbar1.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    //     Compute required size of CoolItems instance
    size = coolitem1.computeSize(size.x, size.y);
    //     Set size for this CoolItem instance
    coolitem1.setSize(size);
    //     The minimum size of the CoolItem is the width of the first button
    coolitem1.setMinimumSize(toolitem1.getWidth(), size.y);

    //     Create second ToolBar instance
    final ToolBar toolbar2 = new ToolBar(coolbar, SWT.NULL);
    //     Create two radio buttons
    final ToolItem toolitem3a = new ToolItem(toolbar2, SWT.RADIO);
    toolitem3a.setText("Radio");
    toolitem3a.setToolTipText("Radio button a");
    final ToolItem toolitem3b = new ToolItem(toolbar2, SWT.RADIO);
    toolitem3b.setText("Radio");
    toolitem3b.setToolTipText("Radio button b");
    //     Create separator
    new ToolItem(toolbar2, SWT.SEPARATOR);
    //     Create drop-down menu button
    final ToolItem toolitem5 = new ToolItem(toolbar2, SWT.DROP_DOWN);
    toolitem5.setText("Drop-down-Menu");
    //     Add event processing to drop-down menu button
    toolitem5.addSelectionListener(
    // In class DropDownSelectionListener we construct the menu
        new DropDownSelectionListener(composite.getShell()));
    // Create second CoolItem, assing Toolbar to it and set size
    final CoolItem coolitem2 = new CoolItem(coolbar, SWT.NULL);
    coolitem2.setControl(toolbar2);
    size = toolbar2.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    size = coolitem2.computeSize(size.x, size.y);
    coolitem2.setSize(size);
    coolitem2.setMinimumSize(toolitem3a.getWidth(), size.y);
    return coolbar;
  }

  class DropDownSelectionListener extends SelectionAdapter {
    private Menu menu;

    private Composite parent;

    public DropDownSelectionListener(Composite parent) {
      this.parent = parent;
    }

    public void widgetSelected(final SelectionEvent e) {
      // Create menu lazily
      if (menu == null) {
        menu = new Menu(parent);
        final MenuItem menuItem1 = new MenuItem(menu, SWT.NULL);
        menuItem1.setText("Item1");
        // Set SelectionListener for menuItem1
        menuItem1.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent m) {
            processMenuEvent(e, menuItem1);
          }
        });
        menuItem1.addArmListener(new ArmListener() {
          public void widgetArmed(ArmEvent m) {
            System.out.println("Mouse is over menu item 1");
          }
        });

        final MenuItem menuItem2 = new MenuItem(menu, SWT.NULL);
        menuItem2.setText("Item2");
        // Set SelectionListener foYr menuItem1
        menuItem2.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent m) {
            processMenuEvent(e, menuItem2);
          }
        });
        menuItem2.addArmListener(new ArmListener() {
          public void widgetArmed(ArmEvent m) {
            System.out.println("Mouse is over menu item 2");
          }
        });
      }
      // Check, if it was the arrow button that was pressed
      if (e.detail == SWT.ARROW) {
        if (menu.isVisible()) {
          // Set visible menu invisible
          menu.setVisible(false);
        } else {
          // Retrieve ToolItem and ToolBar from the event object
          final ToolItem toolItem = (ToolItem) e.widget;
          final ToolBar toolBar = toolItem.getParent();
          // Get position and size of the ToolItem
          Rectangle toolItemBounds = toolItem.getBounds();
          // Convert relative position to absolute position
          Point point = toolBar.toDisplay(new Point(toolItemBounds.x,
              toolItemBounds.y));
          // Set menu position
          menu.setLocation(point.x, point.y + toolItemBounds.height);
          // Make menu visible
          menu.setVisible(true);
        }
      } else {
        final ToolItem toolItem = (ToolItem) e.widget;
        System.out.println("Tool button was pressed: "
            + toolItem.getText());
      }
    }

    private void processMenuEvent(final SelectionEvent e,
        final MenuItem item) {
      // Get text of menu item
      final String s = item.getText();
      // Get ToolItem
      final ToolItem toolItem = (ToolItem) e.widget;
      // Replace ToolItem label with text of the menu item
      toolItem.setText(s);
      // Hide menu
      menu.setVisible(false);
    }
  }

  /** * SashForm ** */

  public static SashForm createSashForm(Shell toplevelShell) {
    //     Create outer SashForm
    SashForm sf1 = new SashForm(toplevelShell, SWT.HORIZONTAL);
    //     Create inner SashForm
    SashForm sf2 = new SashForm(sf1, SWT.VERTICAL);
    //     Create content for vertical SashForm
    List list1 = new List(sf2, SWT.NONE);
    list1.setItems(new String[] { "red", "green", "blue" });
    List list2 = new List(sf2, SWT.NONE);
    list2.setItems(new String[] { "A", "B", "C" });
    //     Apply even weights
    sf2.setWeights(new int[] { 100, 100 });
    //     Create content for horizontal SashForm
    List list3 = new List(sf1, SWT.NONE);
    list3.setItems(new String[] { "one", "two", "three", "four", "five",
        "six" });
    //     Apply uneven weights
    sf1.setWeights(new int[] { 100, 200 });
    return sf1;
  }

  /** * StackLayout ** */

  public static void createStackLayout(Composite composite) {
    //     Neues Composite erzeugen
    final Composite stackComposite = new Composite(composite, SWT.NULL);
    final StackLayout stackLayout = new StackLayout();
    //     Text-Buttons erzeugen
    final Button buttonA = new Button(stackComposite, SWT.PUSH);
    buttonA.setText("Taste A");
    final Button buttonB = new Button(stackComposite, SWT.PUSH);
    buttonB.setText("Taste B");
    //     Auf Klickereignisse reagieren
    buttonA.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        stackLayout.topControl = buttonB;
        //     Neues Layout erzwingen
        stackComposite.layout();
        //     Fokus auf sichtbare Taste setzen
        buttonB.setFocus();
      }
    });
    buttonB.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        stackLayout.topControl = buttonA;
        //     Neues Layout erzwingen
        stackComposite.layout();
        //     Fokus auf sichtbare Taste setzen
        buttonA.setFocus();
      }
    });
    //     Layout initialisieren
    stackLayout.topControl = buttonA;
    stackLayout.marginWidth = 10;
    stackLayout.marginHeight = 5;
    //     Layout setzen
    stackComposite.setLayout(stackLayout);
  }

  /** * PaintListener ** */

  public static void addPaintListener(Composite composite) {
    composite.addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent event) {
        // Get Display intsance from event object
        Display display = event.display;
        // Get a green system color object - we don't
        // need to dispose that
        Color green = display.getSystemColor(SWT.COLOR_DARK_GREEN);
        // Get the graphics context from the event object
        GC gc = event.gc;
        // Set line color
        gc.setForeground(green);
        // Get size of the Composite's client area
        Rectangle rect = ((Composite) event.widget).getClientArea();
        // Now draw an rectangle
        gc.drawRectangle(rect.x + 2, rect.y + 2, rect.width - 4,
            rect.height - 4);
      }
    });
  }

  /** * Fonts ** */

  public static void drawItalic(Composite composite, GC gc) {
    //     Get Display instance
    Display display = composite.getDisplay();
    //     Fetch system font
    Font systemFont = display.getSystemFont();
    //     FontData objects contain the font properties.
    //     With some operating systems a font may possess multiple
    //     FontData instances. We only use the first one.
    FontData[] data = systemFont.getFontData();
    FontData data0 = data[0];
    //     Set the font style to italic
    data0.setStyle(SWT.ITALIC);
    //     Create a new font
    Font italicFont = new Font(display, data0);
    //     Set the new font in the graphics context
    gc.setFont(italicFont);
    //     TODO: call italicFont.dispose() in the DisposeListener
    //     of composite
    //     Draw text at position (4,4) with a transparent background (true).
    gc.drawText("Hello", 4, 4, true);
  }

  /** * Images ** */

  public static void doubleBuffering(Composite composite) {
    //     Create canvas
    final Canvas canvas = new Canvas(composite, SWT.BORDER);
    //     Get white system color
    Color white = canvas.getDisplay().getSystemColor(SWT.COLOR_WHITE);
    //     Set canvas background to white
    canvas.setBackground(white);
    //     Add paint listener
    canvas.addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent e) {
        // Get Display instance from the event object
        Display display = e.display;
        // Get black and red system color - don't dispose these
        Color black = display.getSystemColor(SWT.COLOR_BLACK);
        Color red = display.getSystemColor(SWT.COLOR_RED);
        // Get the graphics context from event object
        GC gc = e.gc;
        // Get the widget that caused the event
        Composite source = (Composite) e.widget;
        // Get the size of this widgets client area
        Rectangle rect = source.getClientArea();
        // Create buffer for double buffering
        Image buffer = new Image(display, rect.width, rect.height);
        // Create graphics context for this buffer
        GC bufferGC = new GC(buffer);
        // perform drawing operations
        bufferGC.setBackground(red);
        bufferGC.fillRectangle(5, 5, rect.width - 10, rect.height - 10);
        bufferGC.setForeground(black);
        bufferGC.drawRectangle(5, 5, rect.width - 10, rect.height - 10);
        bufferGC.setBackground(source.getBackground());
        bufferGC.fillRectangle(10, 10, rect.width - 20,
            rect.height - 20);
        // Now draw the buffered image to the target drawable
        gc.drawImage(buffer, 0, 0);
        // Dispose of the buffer's graphics context
        bufferGC.dispose();
        // Dispose of the buffer
        buffer.dispose();
      }
    });
  }

  /** * Printing ** */

  public static void createPrintButton(final Composite composite) {
    //     Create button for starting printing process
    final Button printButton = new Button(composite, SWT.PUSH);
    printButton.setText("Print");
    //     React to clicks
    printButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        // Get Shell instance
        Shell shell = composite.getShell();
        // Create printer selection dialog
        PrintDialog printDialog = new PrintDialog(shell);
        // and open it
        PrinterData printerData = printDialog.open();
        // Check if OK was pressed
        if (printerData != null) {
          // Create new Printer instance
          Printer printer = new Printer(printerData);
          // Create graphics context for this printer
          GC gc = new GC(printer);
          // Open printing task
          if (!printer.startJob("Hello"))
            System.out.println("Starting printer task failed");
          else {
            // Print first page
            if (!printer.startPage())
              System.out.println("Printing of page 1 failed");
            else {
              // Get green system color from printer
              // and set it as text color
              Color green = printer
                  .getSystemColor(SWT.COLOR_DARK_GREEN);
              gc.setForeground(green);
              // Draw text
              gc.drawText("Hello World", 4, 4, true);
              // Close page
              printer.endPage();
            }
            // Print second page
            if (!printer.startPage())
              System.out.println("Printing of page 2 failed");
            else {
              // Get blue system color from printer
              // and set it as text color
              Color blue = printer.getSystemColor(SWT.COLOR_BLUE);
              gc.setForeground(blue);
              // Draw text
              gc.drawText("Hello Eclipse", 4, 4, true);
              // Close page
              printer.endPage();
            }
            // Close printing task
            printer.endJob();
          }
          // Release operating system resources
          gc.dispose();
          printer.dispose();
        }
      }
    });
  }
}
