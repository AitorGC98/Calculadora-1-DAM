package calculadora;

import com.sun.xml.internal.ws.util.StringUtils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
//import org.jvnet.substance.SubstanceLookAndFeel;
//import static calculadora.convertir.evaluarExpresion;

/**
 *
 * @author Aitor Garcia Curado
 */
public class Calculadora {

    public static void main(String[] args) {
        Marco marco = new Marco();
        marco.setVisible(true);
        
    }
}

class Marco extends JFrame implements ActionListener {

    private JMenuBar menu;
    private JMenu modos;
    private JMenu opciones;
    private JMenuItem menutiem1, menuitem2, menuitem3, menuitem4;
    private PanelBasico panel1;
    private PanelCientifica panel2;

    public Marco() {
        //JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");//establezco estilo para la interfaz 
        } catch (Exception e) {
            e.printStackTrace();
        }

       
        UIManager.put("control", new Color(51, 49, 62));//estilos para el fondo de la interfaz
        UIManager.put("nimbusBase", new Color(50, 100, 200));//estilos para los botones
        
         //creacion de los menus
        menu = new JMenuBar();
        setJMenuBar(menu);
        modos = new JMenu("Modos");
        opciones = new JMenu("Opciones");
        menu.add(opciones);
        menu.add(modos);
        
        //añadir las opciones a los menus 
        menutiem1 = new JMenuItem("Calculadora Básica");
        menuitem2 = new JMenuItem("Calculadora Científica");
        menuitem3 = new JMenuItem("Guardar Como");
        menuitem4 = new JMenuItem("LE");
        menutiem1.addActionListener(this);
        menuitem2.addActionListener(this);
        menuitem3.addActionListener(this);
        menuitem4.addActionListener(this);
        modos.add(menutiem1);
        modos.add(menuitem2);
        opciones.add(menuitem3);
        opciones.add(menuitem4);
        
        Font fuente = new Font("Arial", Font.PLAIN, 15);//fuente para el menu
        modos.setFont(fuente);
        opciones.setFont(fuente);
        
        Toolkit mipantalla = Toolkit.getDefaultToolkit();//toma la pantalla
        Dimension tamanoPantalla = mipantalla.getScreenSize();//toma la dimension de la pantalla
        int altura = tamanoPantalla.height;
        int ancho = tamanoPantalla.width;
        setSize(500, 550);//dimension del marco
        //setSize(ancho / 4, altura / 2);//dimension del marco por resolucion de pantalla, esto falla en algunas pantallas
        setLocation(ancho / 3, altura / 4);//localización en el centro teniendo en cuenta las pantallas
        setTitle("Calculadora");//titulo del marco
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//establezco que hace el boton X de arriba
        Image icono = mipantalla.getImage("icono.jpg");//tomo una imagen
        setIconImage(icono);//uso de la imagen como icono de la ventana
        panel1 = new PanelBasico();//creacion del panel de basica
        panel2 = new PanelCientifica();//creacion del panel de cientifica
        panel1.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel2.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(panel1);//se añade por defecto el panel de basica

    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("Calculadora Básica".equals(command)) {//cambia a la calculadora basica
            remove(panel2);
            add(panel1);
        } else if ("Calculadora Científica".equals(command)) {//cambia a la calculadora cientifica
            remove(panel1);
            add(panel2);
        } else if ("Guardar como".equals(command)) {//guarda todo lo escrito en la calculadora
            guardarHistorial();
        } else if ("LE".equals(command)) {//lee un fichero y pone este en la caluladora en uso para resolverlo

            String result = leerArchivo();
            if (panel1.isShowing()) {//si no funciona usar panel1.isVisible()
                panel1.valor = result;
                panel1.texto1.setText(result);
            } else if (panel2.isShowing()) {
                panel2.valor = result;
                panel2.texto1.setText(result);
            }
        }
        revalidate();
        repaint();
    }

    public String leerArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        int seleccion = fileChooser.showOpenDialog(null);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try {
                BufferedReader br = new BufferedReader(new FileReader(archivo));
                String linea;
                StringBuilder contenido = new StringBuilder();
                while ((linea = br.readLine()) != null) {
                    contenido.append(linea);
                    contenido.append("\n");
                }
                br.close();
                return contenido.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private void guardarHistorial() {
        JFileChooser fileChooser = new JFileChooser();//uso de JFileChooser para guardar donde quiero el fichero
        int seleccion = fileChooser.showSaveDialog(this);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try {
                FileWriter fw = new FileWriter(archivo);
                if (!panel1.historial.isEmpty()) {
                    for (String s : panel1.historial) {
                        fw.write(s + "\n");

                    }
                }
                if (!panel2.historial.isEmpty()) {
                    for (String s : panel2.historial) {
                        fw.write(s + "\n");

                    }
                }
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class PanelBasico extends JPanel implements ActionListener {

        public LinkedList<String> historial = new LinkedList<>();
        private String ANS = "";
        private String valor = "";
        private JTextField texto1;
        private JTextField texto2;
        private double resultado;

        JButton boton7 = new JButton("7");
        JButton boton8 = new JButton("8");
        JButton boton9 = new JButton("9");
        JButton botonDEL = new JButton("DEL");
        JButton botonAC = new JButton("AC");
        JButton boton4 = new JButton("4");
        JButton boton5 = new JButton("5");
        JButton boton6 = new JButton("6");
        JButton botonX = new JButton("X");
        JButton botondiv = new JButton("÷");
        JButton boton1 = new JButton("1");
        JButton boton2 = new JButton("2");
        JButton boton3 = new JButton("3");
        JButton botonsuma = new JButton("+");
        JButton botonresta = new JButton("-");
        JButton boton0 = new JButton("0");
        JButton botonpunto = new JButton(".");
        JButton botonEXP = new JButton("EXP");
        JButton botonANS = new JButton("ANS");
        JButton botonigual = new JButton("=");

        public PanelBasico() {
            setLayout(new BorderLayout());

            Font fuente = new Font("Arial", Font.PLAIN, 25);//establezco la fuente y tamaño de los botones
            boton7.setFont(fuente);
            boton8.setFont(fuente);
            boton9.setFont(fuente);
            boton0.setFont(fuente);
            boton1.setFont(fuente);
            boton2.setFont(fuente);
            boton3.setFont(fuente);
            boton4.setFont(fuente);
            boton5.setFont(fuente);
            boton6.setFont(fuente);
            botonX.setFont(fuente);
            botonDEL.setFont(fuente);
            botonAC.setFont(fuente);
            botonpunto.setFont(fuente);
            botonigual.setFont(fuente);
            botonresta.setFont(fuente);
            botonsuma.setFont(fuente);
            botonEXP.setFont(fuente);
            botondiv.setFont(fuente);
            botonANS.setFont(fuente);
            
            /*Color color = Color.RED;//color botones //Da color a los botones
            botonDEL.setBackground(color);
            botonAC.setBackground(color);*/

            JPanel panel1 = new JPanel();

            panel1.setLayout(new GridLayout(4, 5, 10, 10));
            panel1.add(boton7);
            panel1.add(boton8);
            panel1.add(boton9);
            panel1.add(botonDEL);
            panel1.add(botonAC);
            panel1.add(boton4);
            panel1.add(boton5);
            panel1.add(boton6);
            panel1.add(botonX);
            panel1.add(botondiv);
            panel1.add(boton1);
            panel1.add(boton2);
            panel1.add(boton3);
            panel1.add(botonsuma);
            panel1.add(botonresta);
            panel1.add(boton0);
            panel1.add(botonpunto);
            panel1.add(botonEXP);
            panel1.add(botonANS);
            panel1.add(botonigual);

            JPanel panel2 = new JPanel();
            panel2.setLayout(new GridLayout(2, 1));
            texto1 = new JTextField();
            texto1.setEditable(false);
            panel2.add(texto1);
            texto2 = new JTextField();
            texto2.setEditable(false);
            panel2.add(texto2);

            Font font = new Font("Arial", Font.PLAIN, 50);
            texto1.setFont(font);
            texto2.setFont(font);
            texto1.setColumns(20);
            texto2.setColumns(20);

            add(panel1);
            add(panel2, BorderLayout.NORTH);

            boton7.addActionListener(this);
            boton8.addActionListener(this);
            boton9.addActionListener(this);
            botonDEL.addActionListener(this);
            botonAC.addActionListener(this);
            boton4.addActionListener(this);
            boton5.addActionListener(this);
            boton6.addActionListener(this);
            boton1.addActionListener(this);
            boton2.addActionListener(this);
            boton3.addActionListener(this);
            botonX.addActionListener(this);
            botondiv.addActionListener(this);
            botonsuma.addActionListener(this);
            botonresta.addActionListener(this);
            botonigual.addActionListener(this);
            botonpunto.addActionListener(this);
            botonANS.addActionListener(this);
            botonEXP.addActionListener(this);
            boton0.addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == boton7) {
                valor = valor + "7";
                texto1.setText(valor);
            } else if (e.getSource() == boton8) {
                valor = valor + "8";
                texto1.setText(valor);
            } else if (e.getSource() == boton9) {
                valor = valor + "9";
                texto1.setText(valor);
            } else if (e.getSource() == botonDEL) {
                if (!valor.isEmpty()) { // Verifica si la cadena no está vacía
                    valor = valor.substring(0, valor.length() - 1);
                    texto1.setText(valor);
                }
            } else if (e.getSource() == botonAC) {
                valor = ""; // Borra todo el valor
                texto1.setText(valor);
                texto2.setText(valor);
            } else if (e.getSource() == boton4) {
                valor = valor + "4";
                texto1.setText(valor);
            } else if (e.getSource() == boton5) {
                valor = valor + "5";
                texto1.setText(valor);
            } else if (e.getSource() == boton6) {
                valor = valor + "6";
                texto1.setText(valor);
            } else if (e.getSource() == boton1) {
                valor = valor + "1";
                texto1.setText(valor);
            } else if (e.getSource() == boton2) {
                valor = valor + "2";
                texto1.setText(valor);
            } else if (e.getSource() == boton0) {
                valor = valor + "0";
                texto1.setText(valor);
            } else if (e.getSource() == boton3) {
                valor = valor + "3";
                texto1.setText(valor);
            } else if (e.getSource() == botonX) {
                valor = valor + "*";
                texto1.setText(valor);
            } else if (e.getSource() == botondiv) {
                valor = valor + "/";
                texto1.setText(valor);
            } else if (e.getSource() == botonsuma) {
                valor = valor + "+";
                texto1.setText(valor);
            } else if (e.getSource() == botonresta) {
                valor = valor + "-";
                texto1.setText(valor);
            } else if (e.getSource() == botonpunto) {
                valor = valor + ".";
                texto1.setText(valor);
            } else if (e.getSource() == botonigual) {
                try {
                    ScriptEngineManager mgr = new ScriptEngineManager();
                    ScriptEngine engine = mgr.getEngineByName("JavaScript");
                    String resultado = engine.eval(valor).toString();
                    historial.add(valor);

                    historial.add(resultado);
                    ANS = resultado;
                    texto1.setText("");
                    texto2.setText(resultado);
                    valor = "";
                } catch (ScriptException ex) {
                    // Maneja errores en caso de una expresión matemática inválida
                    texto2.setText("Math Error");
                }
            } else if (e.getSource() == botonANS) {
                valor = valor + ANS;
                texto1.setText(valor);
            } else if (e.getSource() == botonEXP) {
                valor = valor + "E";
                texto1.setText(valor);
            }
        }

    }

    class PanelCientifica extends JPanel implements ActionListener {

        public LinkedList<String> historial = new LinkedList<>();
        LinkedList<String> colaP = new LinkedList<>();
        LinkedList<String> colaV = new LinkedList<>();
        private String ANS = "";
        private String valor = "";
        private String pantalla = "";
        private JTextField texto1;
        private JTextField texto2;
        private double resultado;
        
        JButton boton7 = new JButton("7");
        JButton boton8 = new JButton("8");
        JButton boton9 = new JButton("9");
        JButton botonDEL = new JButton("DEL");
        JButton botonAC = new JButton("AC");
        JButton boton4 = new JButton("4");
        JButton boton5 = new JButton("5");
        JButton boton6 = new JButton("6");
        JButton botonX = new JButton("X");
        JButton botondiv = new JButton("÷");
        JButton boton1 = new JButton("1");
        JButton boton2 = new JButton("2");
        JButton boton3 = new JButton("3");
        JButton botonsuma = new JButton("+");
        JButton botonresta = new JButton("-");
        JButton boton0 = new JButton("0");
        JButton botonpunto = new JButton(".");
        JButton botonEXP = new JButton("EXP");
        JButton botonANS = new JButton("ANS");
        JButton botonigual = new JButton("=");
        JButton botonparaiz = new JButton("(");
        JButton botonparader = new JButton(")");
        JButton botonln = new JButton("ln");
        JButton botonlog = new JButton("log");
        JButton botonMmas = new JButton("<html>M<sup>+</sup></html>");
        JButton botonM = new JButton("M");
        JButton botonraiz = new JButton("√");
        JButton botoncuadrado = new JButton("<html>x<sup>2</sup></html>");
        JButton botonelevado = new JButton("^");
        JButton botoncos = new JButton("cos");
        JButton botonsin = new JButton("sin");
        JButton botontan = new JButton("tan");
        JButton botonfac = new JButton("!");
        JButton botonraizx = new JButton("<html><sup>x</sup>√</html>");
        JButton botonfacto = new JButton("!");
        JButton botonpi = new JButton("π");
        JButton botone = new JButton("e");
        JButton boton10 = new JButton("<html>10<sup>x</sup></html>");
        JButton botonx = new JButton("1/x");

        public PanelCientifica() {

            setLayout(new BorderLayout());

            JPanel panel1 = new JPanel();
            
            Font fuente = new Font("Arial", Font.PLAIN, 15);//establezco la fuente y tamaño de los botones
            boton7.setFont(fuente);
            boton8.setFont(fuente);
            boton9.setFont(fuente);
            boton0.setFont(fuente);
            boton1.setFont(fuente);
            boton2.setFont(fuente);
            boton3.setFont(fuente);
            boton4.setFont(fuente);
            boton5.setFont(fuente);
            boton6.setFont(fuente);
            botonX.setFont(fuente);
            botonDEL.setFont(fuente);
            botonAC.setFont(fuente);
            botonpunto.setFont(fuente);
            botonigual.setFont(fuente);
            botonresta.setFont(fuente);
            botonsuma.setFont(fuente);
            botonEXP.setFont(fuente);
            botondiv.setFont(fuente);
            botonANS.setFont(fuente);
            botonparaiz.setFont(fuente);
            botonparader.setFont(fuente);
            botonln.setFont(fuente);
            botonlog.setFont(fuente);
            botonMmas.setFont(fuente);
            botonM.setFont(fuente);
            botonraiz.setFont(fuente);
            botoncuadrado.setFont(fuente);
            botonelevado.setFont(fuente);
            botoncos.setFont(fuente);
            botonsin.setFont(fuente);
            botontan.setFont(fuente);
            botonfac.setFont(fuente);
            botonraizx.setFont(fuente);
            botonfacto.setFont(fuente);
            botonpi.setFont(fuente);
            botone.setFont(fuente);
            boton10.setFont(fuente);
            botonx.setFont(fuente);

            panel1.setLayout(new GridLayout(4, 5, 10, 10));
            panel1.add(boton7);
            panel1.add(boton8);
            panel1.add(boton9);
            panel1.add(botonDEL);
            panel1.add(botonAC);
            panel1.add(boton4);
            panel1.add(boton5);
            panel1.add(boton6);
            panel1.add(botonX);
            panel1.add(botondiv);
            panel1.add(boton1);
            panel1.add(boton2);
            panel1.add(boton3);
            panel1.add(botonsuma);
            panel1.add(botonresta);
            panel1.add(boton0);
            panel1.add(botonpunto);
            panel1.add(botonEXP);
            panel1.add(botonANS);
            panel1.add(botonigual);
            panel1.setBorder(new EmptyBorder(5, 20, 20, 20));

            JPanel panel3 = new JPanel();//botones cientifica
            panel3.setLayout(new GridLayout(3, 6, 5, 5));
            panel3.add(botonln);
            panel3.add(botonlog);
            panel3.add(botonparaiz);
            panel3.add(botonparader);
            panel3.add(botonMmas);
            panel3.add(botonM);
            panel3.add(botonraiz);
            panel3.add(botoncuadrado);
            panel3.add(botonelevado);
            panel3.add(botoncos);
            panel3.add(botonsin);
            panel3.add(botontan);
            panel3.add(botonraizx);
            panel3.add(botonfac);
            panel3.add(botonpi);
            panel3.add(botone);
            panel3.add(boton10);
            panel3.add(botonx);
            panel3.setBorder(new EmptyBorder(20, 20, 20, 20));

            JPanel panel2 = new JPanel();
            panel2.setLayout(new GridLayout(2, 1));
            texto1 = new JTextField();
            texto1.setEditable(false);
            panel2.add(texto1);
            texto2 = new JTextField();
            texto2.setEditable(false);
            panel2.add(texto2);

            Font font = new Font("Arial", Font.PLAIN, 50);
            texto1.setFont(font);
            texto2.setFont(font);

            add(panel1, BorderLayout.SOUTH);
            add(panel3, BorderLayout.CENTER);
            add(panel2, BorderLayout.NORTH);

            boton7.addActionListener(this);
            boton8.addActionListener(this);
            boton9.addActionListener(this);
            botonDEL.addActionListener(this);
            botonAC.addActionListener(this);
            boton4.addActionListener(this);
            boton5.addActionListener(this);
            boton6.addActionListener(this);
            boton1.addActionListener(this);
            boton2.addActionListener(this);
            boton3.addActionListener(this);
            boton0.addActionListener(this);
            botonX.addActionListener(this);
            botondiv.addActionListener(this);
            botonsuma.addActionListener(this);
            botonresta.addActionListener(this);
            botonigual.addActionListener(this);
            botonpunto.addActionListener(this);
            botonANS.addActionListener(this);
            botonEXP.addActionListener(this);
            botonparader.addActionListener(this);
            botonparaiz.addActionListener(this);
            botonln.addActionListener(this);
            botonlog.addActionListener(this);
            botonM.addActionListener(this);
            botonMmas.addActionListener(this);
            botonraiz.addActionListener(this);
            botoncuadrado.addActionListener(this);
            botonelevado.addActionListener(this);
            botoncos.addActionListener(this);
            botonsin.addActionListener(this);
            botontan.addActionListener(this);
            botonraizx.addActionListener(this);
            botonfac.addActionListener(this);
            botonpi.addActionListener(this);
            botone.addActionListener(this);
            boton10.addActionListener(this);
            botonx.addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == boton7) {
                valor = valor + "7";
                pantalla = pantalla + "7";
                texto1.setText(pantalla);
            } else if (e.getSource() == boton8) {
                valor = valor + "8";
                pantalla = pantalla + "8";
                texto1.setText(pantalla);
            } else if (e.getSource() == boton9) {
                valor = valor + "9";
                pantalla = pantalla + "9";
                texto1.setText(pantalla);
            } else if (e.getSource() == botonDEL) {
                if (!valor.isEmpty()) { // Verifica si la cadena no está vacía
                    valor = valor.substring(0, valor.length() - 1);
                    pantalla = pantalla.substring(0, pantalla.length() - 1);
                    texto1.setText(pantalla);
                }
            } else if (e.getSource() == botonAC) {
                valor = ""; // Borra todo el valor
                pantalla = "";
                texto1.setText(pantalla);
                texto2.setText(pantalla);
            } else if (e.getSource() == boton4) {
                valor = valor + "4";
                pantalla = pantalla + "4";
                texto1.setText(pantalla);
            } else if (e.getSource() == boton5) {
                valor = valor + "5";
                pantalla = pantalla + "5";
                texto1.setText(pantalla);
            } else if (e.getSource() == boton6) {
                valor = valor + "6";
                pantalla = pantalla + "6";
                texto1.setText(pantalla);
            } else if (e.getSource() == boton0) {
                valor = valor + "0";
                pantalla = pantalla + "0";
                texto1.setText(pantalla);
            } else if (e.getSource() == boton1) {
                valor = valor + "1";
                pantalla = pantalla + "1";
                texto1.setText(pantalla);
            } else if (e.getSource() == boton2) {
                valor = valor + "2";
                pantalla = pantalla + "2";
                texto1.setText(pantalla);
            } else if (e.getSource() == boton3) {
                valor = valor + "3";
                pantalla = pantalla + "3";
                texto1.setText(pantalla);
            } else if (e.getSource() == botonX) {
                valor = valor + "*";
                pantalla = pantalla + "x";
                texto1.setText(pantalla);
            } else if (e.getSource() == botondiv) {
                valor = valor + "/";
                pantalla = pantalla + "/";
                texto1.setText(pantalla);
            } else if (e.getSource() == botonsuma) {
                valor = valor + "+";
                pantalla = pantalla + "+";
                texto1.setText(pantalla);
            } else if (e.getSource() == botonresta) {
                valor = valor + "-";
                pantalla = pantalla + "-";
                texto1.setText(pantalla);
            } else if (e.getSource() == botonparaiz) {
                valor = valor + "(";
                pantalla = pantalla + "(";
                texto1.setText(pantalla);
            } else if (e.getSource() == botonparader) {
                valor = valor + ")";
                pantalla = pantalla + ")";
                texto1.setText(pantalla);
            } else if (e.getSource() == botonln) {

                valor = valor + "Math.log(";
                pantalla = pantalla + "ln(";
                texto1.setText(pantalla);

            } else if (e.getSource() == botonlog) {
                try {
                    ScriptEngineManager mgr = new ScriptEngineManager();
                    ScriptEngine engine = mgr.getEngineByName("JavaScript");

                    String resultado = engine.eval(valor).toString();//resuelve la operacion y devuelve el numero en un String
                    historial.add(pantalla);

                    historial.add(resultado);
                    double resultadoNumerico = Double.parseDouble(resultado);//covierte a decimal
                    DecimalFormat decimalFormat = new DecimalFormat("#.#####");
                    decimalFormat.format(resultadoNumerico);

                    resultadoNumerico = Math.log10(resultadoNumerico);
                    valor = decimalFormat.format(resultadoNumerico);

                    ANS = valor;
                    texto1.setText(pantalla);
                    texto2.setText(valor);
                    valor = "";
                    pantalla = "";
                } catch (ScriptException ex) {
                    // Maneja errores en caso de una expresión matemática inválida
                    texto2.setText("Math Error");
                }

            } else if (e.getSource() == botonMmas) {
                colaP.add(pantalla);
                colaV.add(valor);

            } else if (e.getSource() == botonM) {//coge el primero de la cola le da el valor al string lo saca y lo ponemos al final de la cola
                valor = colaV.poll();
                pantalla = colaP.poll();

                colaV.add(valor);
                colaP.add(pantalla);
                texto1.setText(pantalla);

            } else if (e.getSource() == botonraiz) {
                valor = valor + "Math.sqrt(";
                pantalla = pantalla + "√(";
                texto1.setText(pantalla);

            } else if (e.getSource() == botoncuadrado) {
                valor = valor + "*" + valor;
                pantalla = pantalla + "^2";
                texto1.setText(pantalla);

            } else if (e.getSource() == botonpunto) {
                valor = valor + ".";
                pantalla = pantalla + ".";
                texto1.setText(pantalla);
            } else if (e.getSource() == botonelevado) {
                valor = "Math.pow(" + valor + ",";
                pantalla = pantalla + "^(";
                texto1.setText(pantalla);
            } else if (e.getSource() == botoncos) {
                valor = valor + "Math.cos(";
                pantalla = pantalla + "cos(";
                texto1.setText(pantalla);

            } else if (e.getSource() == botonsin) {
                valor = valor + "Math.sin(";
                pantalla = pantalla + "sin(";
                texto1.setText(pantalla);

            } else if (e.getSource() == botontan) {
                valor = valor + "Math.tan(";
                pantalla = pantalla + "tan(";
                texto1.setText(pantalla);

            } else if (e.getSource() == botonraizx) {
                pantalla = pantalla + "√";
                valor = "1/" + valor + "*";
                texto1.setText(pantalla);

            } else if (e.getSource() == botonfac) {
                try {
                    pantalla = pantalla + "!";
                    ScriptEngineManager mgr = new ScriptEngineManager();
                    ScriptEngine engine = mgr.getEngineByName("JavaScript");

                    String resultado = engine.eval(valor).toString();//resuelve la operacion y devuelve el numero en un String
                    historial.add(pantalla);

                    historial.add(resultado);
                    int numero = Integer.parseInt(resultado);
                    int result = 1;
                    for (int i = 1; i <= numero; i++) {
                        result *= i;
                    }

                    resultado = String.valueOf(result);
                    ANS = resultado;
                    texto1.setText(pantalla);
                    texto2.setText(resultado);
                    valor = "";
                    pantalla = "";
                } catch (ScriptException ex) {
                    // Maneja errores en caso de una expresión matemática inválida
                    texto2.setText("Math Error");
                }

            } else if (e.getSource() == botonpi) {
                pantalla = pantalla + "π";
                valor = valor + "Math.PI";
                texto1.setText(pantalla);

            } else if (e.getSource() == botone) {
                pantalla = pantalla + "e";
                valor = valor + "Math.E";
                texto1.setText(pantalla);

            } else if (e.getSource() == boton10) {
                pantalla = pantalla + "10^(";
                valor = valor + "Math.pow(10,";
                texto1.setText(pantalla);

            } else if (e.getSource() == botonx) {
                pantalla = "1/" + pantalla;
                valor = "1/" + valor;
                texto1.setText(pantalla);

            } else if (e.getSource() == botonigual) {
                try {
                    ScriptEngineManager mgr = new ScriptEngineManager();
                    ScriptEngine engine = mgr.getEngineByName("JavaScript");

                    String resultado = engine.eval(valor).toString();//resuelve la operacion y devuelve el numero en un String

                    // Reducción de decimales a 5
                    double resultadoNumerico = Double.parseDouble(resultado);
                    DecimalFormat decimalFormat = new DecimalFormat("#.#####"); // Establece el formato a 5 decimales
                    String resultadoFormateado = decimalFormat.format(resultadoNumerico);
                    historial.add(pantalla);

                    historial.add(resultado);
                    ANS = resultadoFormateado;
                    texto1.setText(pantalla);
                    texto2.setText(resultadoFormateado);
                    valor = "";
                    pantalla = "";
                } catch (ScriptException ex) {
                    // Maneja errores en caso de una expresión matemática inválida
                    texto2.setText("Math Error");
                }
            } else if (e.getSource() == botonANS) {
                valor = valor + ANS;
                pantalla = pantalla + ANS;
                texto1.setText(pantalla);
            } else if (e.getSource() == botonEXP) {
                valor = valor + "E";
                pantalla = pantalla + "E";
                texto1.setText(pantalla);
            }
        }
    }
}
