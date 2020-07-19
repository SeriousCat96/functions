import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.AttributedString;
import java.util.Scanner;


public class Main extends Frame implements ItemListener, ActionListener, AdjustmentListener {
    int scale;              // Пикселей в единице
    int width, height;      // Ширина и высота панели рисования
    int movX = 0, movY = 0;     // Перемещение по сторонам
    float from, to, a, b, c;    // Параметры функции
    String name;            // Название функции
    MenuBar mb;
    Menu fMenu;
    MenuItem ID_SAVE;
    Choice type = new Choice();
    Label size;
    TextField fieldFrom, fieldTo, fieldA, fieldB, fieldC;
    Scrollbar scaleSlider = new Scrollbar(Scrollbar.HORIZONTAL,35,1,5,101);
    Panel upPanel = new Panel();    // Верхняя панель для элементов управления
    Panel downPanel = new Panel();  // Нижняя панель для элементов управления
    Button movR_BTN = new Button(">");
    Button movL_BTN = new Button("<");
    Button movU_BTN = new Button("^");
    Button movD_BTN = new Button("v");
    Button movC_BTN = new Button("Center");
    Button accept_BTN = new Button("Принять изменения");
    Main.Function drawingPanel = new Main.Function();

    public static void main(String[] args) throws FileNotFoundException {
        Main app = new Main();
    }
    Main() throws FileNotFoundException {
        this.setTitle("Графики функций");
        addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        dispose();
                    }

                    public void windowDeactivated(WindowEvent e) {
                        toFront();
                    }
                });
        this.setSize(900, 700);
        this.setBackground(SystemColor.menu);
        this.setLocation(250, 0);
        this.setLayout(new BorderLayout());
        this.add("North",upPanel);
        this.add("Center",drawingPanel);
        this.add("South",downPanel);

        // Загрузка из файла
        Scanner in = new Scanner(new File("input.txt"));
        name = in.nextLine();
        a = in.nextFloat();
        b = in.nextFloat();
        c = in.nextFloat();
        from = in.nextFloat() + movX*scale;
        to = in.nextFloat() + movX*scale;
        in.close();

        fieldA = new TextField(String.valueOf(a));
        fieldA.addActionListener(this);
        fieldB = new TextField(String.valueOf(b));
        fieldB.addActionListener(this);
        fieldC = new TextField(String.valueOf(c));
        fieldC.addActionListener(this);
        fieldFrom = new TextField(String.valueOf(from));
        fieldTo = new TextField(String.valueOf(to));
        fieldFrom.addActionListener(this);
        fieldTo.addActionListener(this);

        ID_SAVE = new MenuItem("Сохранить изображение");
        ID_SAVE.addActionListener(this);
        mb = new MenuBar();
        fMenu = new Menu("Файл");
        fMenu.add(ID_SAVE);
        mb.add(fMenu);
        this.setMenuBar(mb);

        movR_BTN.addActionListener(this);
        movL_BTN.addActionListener(this);
        movU_BTN.addActionListener(this);
        movD_BTN.addActionListener(this);
        movC_BTN.addActionListener(this);
        accept_BTN.addActionListener(this);
        movR_BTN.setBackground(SystemColor.inactiveCaption);
        movL_BTN.setBackground(SystemColor.inactiveCaption);
        movU_BTN.setBackground(SystemColor.inactiveCaption);
        movD_BTN.setBackground(SystemColor.inactiveCaption);
        movC_BTN.setBackground(SystemColor.inactiveCaption);
        accept_BTN.setBackground(SystemColor.inactiveCaption);
        scale = 35;
        scaleSlider.setUnitIncrement(5);
        scaleSlider.addAdjustmentListener(this);
        scaleSlider.setBackground(SystemColor.controlDkShadow);
        size = new Label(String.valueOf(scale));
        type.addItem("...");
        type.addItem("Linear");
        type.addItem("Hyperbole");
        type.addItem("Quadratic");
        type.addItem("SquareRoot");
        type.addItem("Exponential");
        type.addItem("Logarithmic");
        type.addItem("Sin");
        type.addItem("Cos");
        type.addItem("Tan");
        type.addItem("Cot");
        type.addItem("Arcsin");
        type.addItem("Arccos");
        type.addItem("Arctan");
        type.addItem("HyperbolicSin");
        type.addItem("HyperbolicCos");
        type.addItem("HyperbolicTan");
        type.addItemListener(this);
        upPanel.setLayout(new GridBagLayout());
        upPanel.add(new Label("Функция:"), new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 50, 0, 0), 0, 0));
        upPanel.add(type, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 35), 100, 0));
        upPanel.add(new Label("Масштаб (пикселей в 1 ед):"), new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 50, 0, 0), 0, 0));
        upPanel.add(size, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 35), 0, 0));
        upPanel.add(scaleSlider, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 85), 109, 0));
        upPanel.add(new Label("Навигация:"), new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        upPanel.add(movL_BTN, new GridBagConstraints(1, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 5, 0, 147), 0, 0));
        upPanel.add(movR_BTN, new GridBagConstraints(1, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 47, 0, 104), 0, 0));
        upPanel.add(movD_BTN, new GridBagConstraints(1, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 25, 0, 125), 3, 0));
        upPanel.add(movU_BTN, new GridBagConstraints(1, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 25, 0, 125), 3, 0));
        upPanel.add(movC_BTN, new GridBagConstraints(0, 0, 2, 2, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 50), 50, 0));
        downPanel.setLayout(new GridBagLayout());
        downPanel.add(new Label("От:"), new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 50, 0, 0), 0, 0));
        downPanel.add(fieldFrom, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        downPanel.add(new Label("До:"), new GridBagConstraints(2, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        downPanel.add(fieldTo, new GridBagConstraints(3, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        downPanel.add(new Label("a"), new GridBagConstraints(4, 0, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        downPanel.add(fieldA, new GridBagConstraints(5, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        downPanel.add(new Label("b"), new GridBagConstraints(6, 0, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        downPanel.add(fieldB, new GridBagConstraints(7, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        downPanel.add(new Label("с"), new GridBagConstraints(8, 0, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        downPanel.add(fieldC, new GridBagConstraints(9, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 10), 0, 0));
        downPanel.add(accept_BTN, new GridBagConstraints(10, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 10), 0, 0));
        setVisible(true);
    }
    @Override
    public void itemStateChanged(ItemEvent e){
        if (e.getItemSelectable() == type){
            name =(String)e.getItem();
            if (!name.equals("Linear"))
                fieldC.setEnabled(true);
            else fieldC.setEnabled(false);
            drawingPanel.repaint();
        }
    }
    @Override
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == ID_SAVE){
            try {
                save("screen");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        if (e.getSource() == movL_BTN){
            movX += 1;
            drawingPanel.repaint();
        }
        if (e.getSource() == movR_BTN){
            movX -= 1;
            drawingPanel.repaint();
        }
        if (e.getSource() == movU_BTN){
            movY += 1;
            drawingPanel.repaint();
        }
        if (e.getSource() == movD_BTN){
            movY -= 1;
            drawingPanel.repaint();
        }
        if (e.getSource() == movC_BTN){
            movX = 0; movY = 0;
            drawingPanel.repaint();
        }
        if (e.getSource() == accept_BTN){
            a = Float.parseFloat(fieldA.getText());
            b = Float.parseFloat(fieldB.getText());
            c = Float.parseFloat(fieldC.getText());
            from = getWidth()/2 + Math.round(Float.parseFloat(fieldFrom.getText())*scale)+movX*scale;
            drawingPanel.repaint();
        }
    }
    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (e.getAdjustable() == scaleSlider){
            scale = scaleSlider.getValue();
            size.setText(String.valueOf(scale));
            drawingPanel.repaint();
        }
    }
    public void save(String filename) throws FileNotFoundException {
        Scanner in = new Scanner(new File("nums.abc"));
        int num = in.nextInt();
        in.close();
        BufferedImage image = (BufferedImage)
                drawingPanel.createImage (drawingPanel.getWidth (), drawingPanel.getHeight ());
        Graphics2D g2 = image.createGraphics ();
        drawingPanel.paint (g2);
        g2.dispose();
        try {
            ImageIO.write(image, "BMP", new File(filename + num++ +".bmp"));
            PrintWriter out = new PrintWriter("nums.abc");
            out.print(num);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    class Function extends Canvas{
        int x, y, oldX, oldY;
        public void line(float a, float b){
            y = (int) (height/2 - ((double) a*(x - width/2)) - b*scale);
        }
        public void hyp(float a, float b, float c){
            y = (int) (height/2 - ((double) a*scale /(x - width/2 + b*scale)*scale) - c*scale);
        }
        public void quad(float a, float b, float c){
            y = (int) (height/2 - a*Math.pow((double)(x - width/2), 2)/ scale - b*(x-width/2) - c*scale);
        }
        public void exp(float a, float b, float c){
            y = (int) (height/2 - Math.pow(a, (double)b*(x - width/2)/scale)*scale - c*scale);
        }
        public void sqrt(float a, float b, float c){
            y = (int) (height/2 - a*Math.sqrt((double) b*(x - width / 2/*- b*scale*/) / scale) * scale - c*scale);
        }
        public void log(float a, float b, float c){
            y = (int)(height/2 - Math.log((double)(b*(x - width/2)/scale))/Math.log(a)*scale - c*scale);
        }
        public void asin(float a, float b, float c){
            y = (int)(height/2 - a*Math.asin((double) (x - width / 2 + b*scale) / scale) * scale - c*scale);
        }
        public void acos(float a, float b, float c){
            y = (int)(height/2 - a*Math.acos((double) (x - width / 2 + b*scale) / scale) * scale - c*scale);
        }
        public void atan(float a, float b, float c){
            y = (int) (height/2 - a*Math.atan((double) b*(x - width / 2) / scale) * scale - c*scale);
        }
        public void sin(float a, float b, float c){
            y = (int) (height/2 - a*Math.sin((double) b*(x - width / 2) / scale) * scale - c*scale);
        }
        public void cos(float a, float b, float c){
            y = (int) (height/2 - a*Math.cos((double) b*(x - width / 2) / scale) * scale - c*scale);
        }
        public void tan(float a, float b, float c){
            y = (int) (height/2 - a*Math.tan((double) b*(x - width / 2) / scale) * scale - c*scale);
        }
        public void cot(float a, float b, float c){
            y = (int) (height/2 - a/(Math.tan((double) b*(x - width / 2) / scale)) * scale - c*scale);
        }
        public void sinh(float a, float b, float c){
            y = (int) (height/2 - a*Math.sinh((double) b*(x - width / 2) / scale) * scale - c*scale);
        }
        public void cosh(float a, float b, float c){
            y = (int) (height/2 - a*Math.cosh((double) b*(x - width / 2) / scale) * scale - c*scale);
        }
        public void tanh(float a, float b, float c){
            y = (int) (height/2 - a*Math.tanh((double) b*(x - width / 2) / scale) * scale - c*scale);
        }
        public void drawFunc(Graphics g){
            if (a == 0 && !name.equals("Linear"))
                return;
            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            String sA = fieldA.getText();
            String sB = fieldB.getText();
            if (b > 0)
                sB = "+"+sB;
            else if (b == 0)
                sB = "";
            String sC = fieldC.getText();
            if (c > 0)
                sC = "+"+sC;
            else if (c == 0)
                sC = "";
            switch (name) {
                case "Linear":
                    g.drawString("y = ax + b", width/20, 19*height/20);
                    if (a == 1)
                        sA = "";
                    if (a == -1)
                        sA = "-";
                    if (a != 0)
                        g.drawString("y = "+sA+"x"+sB, width/20, height/20);
                    else {
                        sB = sB.substring(1);
                        g.drawString("y = "+sB, width/20, height/20);
                    }
                    break;
                case "Hyperbole":
                    g.drawString("y = (a/(x + b)) + c", width/20, 19*height/20);
                    g.drawString("y = ("+sA+"/(x"+sB+"))"+sC, width/20, height/20);
                    break;
                case "Quadratic":
                    g.drawString("y = ax² + bx + c", width/20, 19*height/20);
                    if (a == 1)
                        sA = "";
                    if (a == -1)
                        sA = "-";
                    if (b == 1)
                        sB = "+";
                    if (b == -1)
                        sB = "-";
                    if (b != 0)
                        g.drawString("y = "+sA+"x²"+sB+"x"+sC, width/20, height/20);
                    else g.drawString("y = "+sA+"x²"+sC, width/20, height/20);
                    break;
                case "SquareRoot":
                    g.drawString("y = a√(bx) + c", width/20, 19*height/20);
                    if (b <= 0)
                        return;
                    if (a == 1)
                        sA = "";
                    if (a == -1)
                        sA = "-";
                    sB = sB.substring(1);
                    if (b == 1)
                        sB = "";
                    g.drawString("y = "+sA+"√("+sB+"x)"+sC, width/20, height/20);
                    break;
                case "Exponential": {
                    String s = "y = abx + c";
                    AttributedString as = new AttributedString(s);
                    as.addAttribute(TextAttribute.SIZE, 16, 0, s.length());
                    as.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, 5, 7);
                    as.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                    g.drawString(as.getIterator(), width/20, 19*height/20);
                    if(a <= 0 || a == 1 || b == 0)
                        return;
                    if (b == 1)
                        sB = "";
                    else if (b > 0)
                        sB = sB.substring(1);
                    if (b == -1)
                        sB = "-";
                    String str = "y = "+sA+sB+"x"+sC+" ";
                    as = new AttributedString(str);
                    as.addAttribute(TextAttribute.SIZE, 16, 0, str.length());
                    as.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, sA.length() + 4, str.length() - (sC + 1).length());
                    as.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                    g.drawString(as.getIterator(), width / 20, height / 20);
                    break;
                }
                case "Logarithmic":
                    String s = "y = loga(bx) + c";
                    AttributedString as = new AttributedString(s);
                    as.addAttribute(TextAttribute.SIZE, 16, 0, s.length());
                    as.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 7, 8);
                    as.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                    g.drawString(as.getIterator(), width/20, 19*height/20);
                    if (!(a > 0 && a != 1) || b <= 0)
                        return;
                    sB = sB.substring(1);
                    if (b == 1)
                        sB = "";
                    String str = "y = log"+sA+"("+sB+"x)"+sC+" ";
                    as = new AttributedString(str);
                    as.addAttribute(TextAttribute.SIZE, 16, 0, str.length());
                    as.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB,7,str.length()-("("+sB+"x)"+sC+" ").length());
                    as.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                    g.drawString(as.getIterator(),width/20, height/20);
                    break;
                case "Sin":
                    g.drawString("y = a*sin(bx) + c", width/20, 19*height/20);
                    if (b == 0)
                        return;
                    if (a == 1)
                        sA = "";
                    if (a == -1)
                        sA = "-";
                    if (b == 1)
                        sB = "";
                    else if (b > 0)
                        sB = sB.substring(1);
                    if (b == -1)
                        sB = "-";
                    g.drawString("y = "+sA+"sin("+sB+"x)"+sC, width/20, height/20);
                    break;
                case "Cos":
                    g.drawString("y = a*cos(bx) + c", width/20, 19*height/20);
                    if (b == 0)
                        return;
                    if (a == 1)
                        sA = "";
                    if (a == -1)
                        sA = "-";
                    if (b == 1)
                        sB = "";
                    else if (b > 0)
                        sB = sB.substring(1);
                    if (b == -1)
                        sB = "-";
                    g.drawString("y = "+sA+"cos("+sB+"x)"+sC, width/20, height/20);
                    break;
                case "Tan":
                    g.drawString("y = a*tan(bx) + c", width/20, 19*height/20);
                    if (b == 0)
                        return;
                    if (a == 1)
                        sA = "";
                    if (a == -1)
                        sA = "-";
                    if (b == 1)
                        sB = "";
                    else if (b > 0)
                        sB = sB.substring(1);
                    if (b == -1)
                        sB = "-";
                    g.drawString("y = "+sA+"tan("+sB+"x)"+sC, width/20, height/20);
                    break;
                case "Cot":
                    g.drawString("y = a*cot(bx) + c", width/20, 19*height/20);
                    if (b == 0)
                        return;
                    if (a == 1)
                        sA = "";
                    if (a == -1)
                        sA = "-";
                    if (b == 1)
                        sB = "";
                    else if (b > 0)
                        sB = sB.substring(1);
                    if (b == -1)
                        sB = "-";
                    g.drawString("y = "+sA+"cot("+sB+"x)"+sC, width/20, height/20);
                    break;
                case "Arcsin":
                    g.drawString("y = a*arcsin(x + b) + c", width/20, 19*height/20);
                    if (a == 1)
                        sA = "";
                    if (a == -1)
                        sA = "-";
                    g.drawString("y = "+sA+"arcsin(x"+sB+")"+sC, width/20, height/20);
                    break;
                case "Arccos":
                    g.drawString("y = a*arccos(x + b) + c", width/20, 19*height/20);
                    if (a == 1)
                        sA = "";
                    if (a == -1)
                        sA = "-";
                    g.drawString("y = "+sA+"arccos(x"+sB+")"+sC, width/20, height/20);
                    break;
                case "Arctan":
                    g.drawString("y = a*arctan(bx) + c", width/20, 19*height/20);
                    if (b == 0)
                        return;
                    if (a == 1)
                        sA = "";
                    if (a == -1)
                        sA = "-";
                    if (b == 1)
                        sB = "";
                    else if (b > 0)
                        sB = sB.substring(1);
                    if (b == -1)
                        sB = "-";
                    g.drawString("y = "+sA+"arctan("+sB+"x)"+sC, width/20, height/20);
                    break;
                case "HyperbolicSin":
                    g.drawString("y = a*sinh(bx) + c", width/20, 19*height/20);
                    if (a == 1)
                        sA = "";
                    if (a == -1)
                        sA = "-";
                    if (b == 1)
                        sB = "";
                    else if (b > 0)
                        sB = sB.substring(1);
                    if (b == -1)
                        sB = "-";
                    g.drawString("y = "+sA+"sinh("+sB+"x)"+sC, width/20, height/20);
                    break;
                case "HyperbolicCos":
                    g.drawString("y = a*cosh(bx) + c", width/20, 19*height/20);
                    if (b == 0)
                        return;
                    if (a == 1)
                        sA = "";
                    if (a == -1)
                        sA = "-";
                    if (b == 1)
                        sB = "";
                    else if (b > 0)
                        sB = sB.substring(1);
                    if (b == -1)
                        sB = "-";
                    g.drawString("y = "+sA+"cosh("+sB+"x)"+sC, width/20, height/20);
                    break;
                case "HyperbolicTan":
                    g.drawString("y = a*tanh(bx) + c", width/20, 19*height/20);
                    if (b == 0)
                        return;
                    if (a == 1)
                        sA = "";
                    if (a == -1)
                        sA = "-";
                    if (b == 1)
                        sB = "";
                    else if (b > 0)
                        sB = sB.substring(1);
                    if (b == -1)
                        sB = "-";
                    g.drawString("y = "+sA+"tanh("+sB+"x)"+sC, width/20, height/20);
                    break;
                default:
                    break;
            }
            g.drawString(fieldFrom.getText()+" ≤ x ≤ "+fieldTo.getText(), width/20, height/20+20);
        }
        public void drawGrid(Graphics g){
            // Координатная сетка
            g.setColor(new Color(153, 169, 172));
            for(int i=width/2, j=height/2;i<width || j<height; i+=scale, j+=scale){
                g.drawLine(i, 0, i, height);
                g.drawLine(0, j, width, j);
            }
            for(int i=width/2, j=height/2;i>0 || j>0; i-=scale, j-=scale){
                g.drawLine(i, 0, i, height);
                g.drawLine(0, j, width, j);
            }
        }
        public void drawAxis(Graphics g){
            // Оси отрезка
            g.setColor(Color.blue);
            g.drawLine(Math.round(from) + movX*scale, 0, Math.round(from) + movX*scale, height);
            g.drawLine(Math.round(to) + movX*scale, 0, Math.round(to) + movX*scale, height);
            // Координатные оси
            g.setColor(Color.black);
            g.drawLine(width / 2 + movX*scale, 0, width / 2 + movX*scale, height);
            g.drawLine(0, height/2+movY*scale, width, height/2+movY*scale);
        }
        public void drawGraphic(Graphics g){
            if(a == 0 && !name.equals("Linear"))
                return;
            if(name.equals("Arcsin") || name.equals("Arccos"))
                if(from <= width/2 - (b + 1)*scale)
                    from = width / 2 - (b + 1) * scale;
            if(name.equals("Logarithmic"))
                from = getWidth()/2;
            if(name.equals("SquareRoot"))
                from = getWidth()/2;
            //g.setColor(new Color(226, 0, 2));
            g.setColor(Color.red);
            for (x = Math.round(from); x <= to; x++) {
                switch (name) {
                    case "Linear":
                        line(a,b);
                        break;
                    case "Hyperbole":
                        hyp(a,b,c);
                        break;
                    case "Quadratic":
                        quad(a,b,c);
                        break;
                    case "SquareRoot":
                        if(b > 0)
                            sqrt(a,b,c);
                        else return;
                        break;
                    case "Exponential":
                        if(a != 1 && a > 0)
                            exp(a,b,c);
                        else return;
                        break;
                    case "Logarithmic":
                        if(a > 0 && a != 1)
                            log(a,b,c);
                        else return;
                        break;
                    case "Sin":
                        if(b != 0)
                            sin(a,b,c);
                        else return;
                        break;
                    case "Cos":
                        if(b != 0)
                            cos(a,b,c);
                        else return;
                        break;
                    case "Tan":
                        if(b != 0)
                            tan(a,b,c);
                        else return;
                        break;
                    case "Cot":
                        if(b != 0)
                            cot(a,b,c);
                        else return;
                        break;
                    case "Arcsin":
                        if(x <= width/2 - (b-1)*scale)
                            asin(a,b,c);
                        else return;
                        break;
                    case "Arccos":
                        if(x <= width/2 - (b-1)*scale)
                            acos(a,b,c);
                        else return;
                        break;
                    case "Arctan":
                        atan(a,b,c);
                        break;
                    case "HyperbolicSin":
                        sinh(a,b,c);
                        break;
                    case "HyperbolicCos":
                        cosh(a,b,c);
                        break;
                    case "HyperbolicTan":
                        tanh(a,b,c);
                        break;
                    default:
                        return;
                }
                if (x == from) {
                    oldX = x;
                    oldY = y;
                }
                g.drawLine(oldX+movX*scale, oldY+movY*scale, x+movX*scale, y+movY*scale);
                oldX = x;
                oldY = y;
            }
        }
        public void paint(Graphics g) {
            setBackground(new Color(200, 255, 241));
            width = getSize().width;
            height = getSize().height;
            from = (width/2) + Math.round(Float.parseFloat(fieldFrom.getText())*scale);
            to = (width/2) + Math.round(Float.parseFloat(fieldTo.getText())*scale);
            drawGrid(g);
            drawAxis(g);
            drawGraphic(g);
            drawFunc(g);
        }
    }
}
