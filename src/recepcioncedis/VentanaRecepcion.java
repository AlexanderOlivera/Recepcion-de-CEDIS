package recepcioncedis;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class VentanaRecepcion extends JFrame {

    // --- Lógica del Negocio ---
    private List<Bulto> bultosRecibidosOK;
    private List<Bulto> bultosSobrantes;
    private List<Canastilla> canastillasRecibidasOK; 
    private List<Canastilla> canastillasSobrantes;   
    
    private BaseDatosBultos baseDeDatos;
    private MaestroProductos maestroProductos;
    private String idTransferenciaSeleccionada;
    
    private int modoEscaneo = 0; // 0 = Bultos, 1 = Canastillas

    // --- Componentes Visuales ---
    private JPanel mainContainer;
    private CardLayout cardLayout;
    
    // UI Escaneo
    private JTextArea areaResumen;
    private JTextField txtCodigoBulto;
    private JLabel lblRutaDinamica;
    private JLabel lblTabBultos;      
    private JLabel lblTabCanastillas; 
    private JLabel lblTituloLista;
    private JButton btnAccionPrincipal;

    // UI Reporte Final
    private JTextArea areaReporteFinal;

    // Colores
    private final Color COLOR_ROJO_OXXO = new Color(230, 0, 0); 
    private final Color COLOR_AZUL_OSCURO = new Color(45, 45, 140);
    private final Color COLOR_FONDO = Color.WHITE;
    private final Color COLOR_TEXTO_GRIS = new Color(100, 100, 100);

    public VentanaRecepcion() {
        // Inicializar Listas y BD
        this.bultosRecibidosOK = new ArrayList<>();
        this.bultosSobrantes = new ArrayList<>();
        this.canastillasRecibidasOK = new ArrayList<>();
        this.canastillasSobrantes = new ArrayList<>();
        
        this.baseDeDatos = new BaseDatosBultos();
        this.maestroProductos = new MaestroProductos();

        // Configuración Ventana
        setTitle("Recepción CEDIS");
        setSize(420, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Sistema de Pantallas
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Agregamos las 3 pantallas al flujo
        mainContainer.add(crearPantallaSeleccion(), "SELECCION");
        mainContainer.add(crearPantallaEscaneo(), "ESCANEO");
        mainContainer.add(crearPantallaReporte(), "REPORTE"); // <--- NUEVA PANTALLA

        this.add(mainContainer);
        cardLayout.show(mainContainer, "SELECCION");
    }

    // ==========================================
    // PANTALLA 1: SELECCIÓN
    // ==========================================
    private JPanel crearPantallaSeleccion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);

        panel.add(crearHeader("SELECCIONAR TRANSFERENCIA"));
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(COLOR_FONDO);
        content.setBorder(new EmptyBorder(50, 30, 50, 30));

        JLabel lblInstruccion = new JLabel("<html><div style='text-align:center;'>Seleccione el número de<br>Transferencia o Bol que desea recibir:</div></html>");
        lblInstruccion.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblInstruccion.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] opciones = baseDeDatos.getListaIDs();
        JComboBox<String> comboTransferencias = new JComboBox<>(opciones);
        comboTransferencias.setFont(new Font("SansSerif", Font.BOLD, 14));
        comboTransferencias.setMaximumSize(new Dimension(300, 40));
        comboTransferencias.setAlignmentX(Component.CENTER_ALIGNMENT);
        comboTransferencias.setBackground(Color.WHITE);

        JButton btnIniciar = new JButton("INICIAR RECEPCIÓN");
        estilizarBoton(btnIniciar, COLOR_ROJO_OXXO);
        btnIniciar.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        btnIniciar.addActionListener(e -> {
            String seleccionado = (String) comboTransferencias.getSelectedItem();
            if (seleccionado != null) iniciarRecepcion(seleccionado);
        });

        content.add(lblInstruccion);
        content.add(Box.createVerticalStrut(30));
        content.add(comboTransferencias);
        content.add(Box.createVerticalStrut(50));
        content.add(btnIniciar);

        panel.add(content);
        return panel;
    }

    // ==========================================
    // PANTALLA 2: ESCANEO
    // ==========================================
    private JPanel crearPantallaEscaneo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);

        panel.add(crearHeader("RECEPCIÓN DE PEDIDO CEDIS"));

        // Panel Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(COLOR_FONDO);
        infoPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblLogo = new JLabel("<html><div style='background-color:#F4F4F4; padding:5px 15px; text-align:center;'><span style='color:red; font-size:28px; font-weight:900;'>OXXO</span><br><span style='font-size:10px; color:black;'>Tienda</span></div></html>", SwingConstants.CENTER);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblRuta = createInfoLabel("RUTA: 4567890", false);
        JLabel lblCR = createInfoLabel("CR Tienda: 505GY", false);
        lblRutaDinamica = createInfoLabel("TRANSFERENCIA: ...", false);
        lblRutaDinamica.setForeground(COLOR_TEXTO_GRIS);
        JLabel lblNombre = createInfoLabel("Nombre Tienda: ESTACION VIGA", false);

        infoPanel.add(lblLogo);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(lblRuta);
        infoPanel.add(lblCR);
        infoPanel.add(lblRutaDinamica);
        infoPanel.add(lblNombre);

        // Panel Input
        JPanel searchSection = new JPanel(new BorderLayout(0, 5));
        searchSection.setBackground(COLOR_FONDO);
        searchSection.setBorder(new EmptyBorder(5, 20, 5, 20));
        searchSection.setMaximumSize(new Dimension(420, 80));

        JLabel lblTransferencia = new JLabel("Escanear Código:");
        lblTransferencia.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTransferencia.setForeground(new Color(60, 60, 60));

        JPanel inputWrapper = new JPanel(new BorderLayout());
        inputWrapper.setBackground(new Color(245, 245, 245));

        txtCodigoBulto = new JTextField("");
        txtCodigoBulto.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtCodigoBulto.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 1, 1, 0, Color.LIGHT_GRAY),
            new EmptyBorder(10, 10, 10, 10)
        ));
        txtCodigoBulto.setBackground(new Color(250, 250, 250));
        
        JButton btnBuscar = new JButton("..."); 
        // Nota: Para el botón pequeño de búsqueda, usamos estilo manual simple
        btnBuscar.setBackground(COLOR_AZUL_OSCURO);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnBuscar.setBorderPainted(false);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setOpaque(true); // <-- IMPORTANTE
        btnBuscar.setPreferredSize(new Dimension(50, 45));
        btnBuscar.setVerticalAlignment(SwingConstants.BOTTOM); 
        
        btnBuscar.addActionListener(e -> escanear());
        txtCodigoBulto.addActionListener(e -> escanear());

        inputWrapper.add(txtCodigoBulto, BorderLayout.CENTER);
        inputWrapper.add(btnBuscar, BorderLayout.EAST);
        searchSection.add(lblTransferencia, BorderLayout.NORTH);
        searchSection.add(inputWrapper, BorderLayout.CENTER);

        // Panel Lista con Tabs
        JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setBackground(COLOR_FONDO);
        listContainer.setBorder(new EmptyBorder(5, 20, 10, 20));

        JPanel tabsPanel = new JPanel(new GridLayout(1, 2));
        tabsPanel.setPreferredSize(new Dimension(420, 35));
        
        lblTabBultos = createTabLabel("BULTOS", true);
        lblTabCanastillas = createTabLabel("CANASTILLAS", false);
        
        lblTabBultos.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { cambiarModo(0); }
        });
        lblTabCanastillas.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { cambiarModo(1); }
        });

        tabsPanel.add(lblTabBultos);
        tabsPanel.add(lblTabCanastillas);

        lblTituloLista = new JLabel("  LISTA DE BULTOS", SwingConstants.LEFT);
        lblTituloLista.setOpaque(true);
        lblTituloLista.setBackground(COLOR_AZUL_OSCURO);
        lblTituloLista.setForeground(Color.WHITE);
        lblTituloLista.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTituloLista.setPreferredSize(new Dimension(100, 30));

        areaResumen = new JTextArea();
        areaResumen.setEditable(false);
        areaResumen.setFont(new Font("SansSerif", Font.PLAIN, 14));
        areaResumen.setBorder(new EmptyBorder(10, 10, 10, 10)); 

        JScrollPane scrollPane = new JScrollPane(areaResumen);
        scrollPane.setBorder(new MatteBorder(0, 1, 1, 1, new Color(220, 220, 220))); 

        JPanel listInternal = new JPanel(new BorderLayout());
        listInternal.add(lblTituloLista, BorderLayout.NORTH);
        listInternal.add(scrollPane, BorderLayout.CENTER);

        listContainer.add(tabsPanel, BorderLayout.NORTH);
        listContainer.add(listInternal, BorderLayout.CENTER);

        // Footer Dinámico
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(COLOR_FONDO);
        footerPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        footerPanel.setMaximumSize(new Dimension(420, 80));
        
        btnAccionPrincipal = new JButton("SIGUIENTE: CANASTILLAS >>");
        estilizarBoton(btnAccionPrincipal, COLOR_ROJO_OXXO); // Aplicamos el estilo corregido
        btnAccionPrincipal.addActionListener(e -> avanzarFase());

        JButton btnVolver = new JButton("<");
        btnVolver.setBackground(Color.GRAY);
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false); // Quitar borde para que se vea plano
        btnVolver.setOpaque(true); // Hacer opaco
        btnVolver.setPreferredSize(new Dimension(50, 50));
        btnVolver.addActionListener(e -> cardLayout.show(mainContainer, "SELECCION"));

        footerPanel.add(btnVolver, BorderLayout.WEST);
        footerPanel.add(Box.createHorizontalStrut(10), BorderLayout.CENTER);
        footerPanel.add(btnAccionPrincipal, BorderLayout.CENTER);

        panel.add(infoPanel);
        panel.add(searchSection);
        panel.add(listContainer);
        panel.add(footerPanel);

        return panel;
    }

    // ==========================================
    // PANTALLA 3: REPORTE FINAL (NUEVA)
    // ==========================================
    private JPanel crearPantallaReporte() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(COLOR_FONDO);

        // Header
        panel.add(crearHeader("REPORTE DE RECEPCIÓN"), BorderLayout.NORTH);

        // Área de Texto Estilizada
        areaReporteFinal = new JTextArea();
        areaReporteFinal.setEditable(false);
        areaReporteFinal.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Fuente monoespaciada para alinear
        areaReporteFinal.setMargin(new Insets(15, 15, 15, 15));
        
        JScrollPane scrollReporte = new JScrollPane(areaReporteFinal);
        scrollReporte.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollReporte, BorderLayout.CENTER);

        // Botón Salir
        JPanel footer = new JPanel();
        footer.setBackground(COLOR_FONDO);
        footer.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JButton btnFinalizarTodo = new JButton("VOLVER AL INICIO");
        estilizarBoton(btnFinalizarTodo, COLOR_AZUL_OSCURO); // Usar el estilo corregido
        btnFinalizarTodo.addActionListener(e -> cardLayout.show(mainContainer, "SELECCION"));
        
        footer.add(btnFinalizarTodo);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    // --- LÓGICA ---

    private void iniciarRecepcion(String idTransferencia) {
        this.idTransferenciaSeleccionada = idTransferencia;
        baseDeDatos.setTransferenciaActual(idTransferencia);
        
        bultosRecibidosOK.clear();
        bultosSobrantes.clear();
        canastillasRecibidasOK.clear();
        canastillasSobrantes.clear();
        
        txtCodigoBulto.setText("");
        lblRutaDinamica.setText("TRANSFERENCIA: " + idTransferencia);
        
        cambiarModo(0);
        cardLayout.show(mainContainer, "ESCANEO");
        txtCodigoBulto.requestFocus();
    }

    private void avanzarFase() {
        if (modoEscaneo == 0) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Finalizar Bultos y pasar a Canastillas?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) cambiarModo(1);
        } else {
            generarReporteFinal(); // <-- Aquí llamamos al reporte en lugar de un popup
        }
    }

    private void generarReporteFinal() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sb.append("=========================================\n");
        sb.append("         REPORTE DE RECEPCIÓN\n");
        sb.append("=========================================\n");
        sb.append("Fecha: ").append(sdf.format(new Date())).append("\n");
        sb.append("Transferencia: ").append(idTransferenciaSeleccionada).append("\n\n");

        // --- BULTOS ---
        sb.append("---------- BULTOS ----------\n");
        sb.append("[ ✔ ] RECIBIDOS: ").append(bultosRecibidosOK.size()).append("\n");
        for(Bulto b : bultosRecibidosOK) sb.append("  - ").append(b.getNombre()).append("\n");
        
        sb.append("\n[ ⚠ ] SOBRANTES: ").append(bultosSobrantes.size()).append("\n");
        for(Bulto b : bultosSobrantes) sb.append("  - ").append(b.getNombre()).append("\n");

        sb.append("\n[ ❌ ] FALTANTES:\n");
        int faltantesB = 0;
        for (String key : baseDeDatos.getBultosEsperados().keySet()) {
            boolean encontrado = false;
            for(Bulto b : bultosRecibidosOK) if(b.getCodigo().equals(key)) encontrado = true;
            if(!encontrado) {
                sb.append("  - ").append(baseDeDatos.getNombreBulto(key)).append(" (").append(key).append(")\n");
                faltantesB++;
            }
        }
        if(faltantesB == 0) sb.append("  (Ninguno)\n");

        // --- CANASTILLAS ---
        sb.append("\n\n------- CANASTILLAS -------\n");
        sb.append("[ ✔ ] RECIBIDAS: ").append(canastillasRecibidasOK.size()).append("\n");
        for(Canastilla c : canastillasRecibidasOK) sb.append("  - ").append(c.getTipo()).append("\n");

        sb.append("\n[ ⚠ ] SOBRANTES: ").append(canastillasSobrantes.size()).append("\n");
        for(Canastilla c : canastillasSobrantes) sb.append("  - ").append(c.getTipo()).append("\n");

        sb.append("\n[ ❌ ] FALTANTES:\n");
        int faltantesC = 0;
        for (String key : baseDeDatos.getCanastillasEsperadas().keySet()) {
            boolean encontrado = false;
            for(Canastilla c : canastillasRecibidasOK) if(c.getCodigo().equals(key)) encontrado = true;
            if(!encontrado) {
                sb.append("  - ").append(baseDeDatos.getTipoCanastilla(key)).append(" (").append(key).append(")\n");
                faltantesC++;
            }
        }
        if(faltantesC == 0) sb.append("  (Ninguno)\n");

        sb.append("\n=========================================\n");
        sb.append("ESTATUS FINAL: ");
        if(faltantesB == 0 && faltantesC == 0) sb.append("COMPLETO - SIN FALTANTES");
        else sb.append("INCOMPLETO - REVISAR FALTANTES");

        // Mostrar en la pantalla de reporte
        areaReporteFinal.setText(sb.toString());
        cardLayout.show(mainContainer, "REPORTE");
    }

    private void cambiarModo(int nuevoModo) {
        this.modoEscaneo = nuevoModo;
        txtCodigoBulto.setText("");
        
        if (modoEscaneo == 0) { 
            lblTituloLista.setText("  LISTA DE BULTOS");
            lblTituloLista.setBackground(COLOR_AZUL_OSCURO); 
            estilizarTabActivo(lblTabBultos); estilizarTabInactivo(lblTabCanastillas);
            btnAccionPrincipal.setText("SIGUIENTE: CANASTILLAS >>");
            // Nota: Aquí no usamos estilizarBoton porque necesitamos cambiar el color dinámicamente,
            // pero nos aseguramos de setOpaque(true)
            btnAccionPrincipal.setBackground(new Color(0, 102, 204)); 
            btnAccionPrincipal.setOpaque(true);
            btnAccionPrincipal.setBorderPainted(false);
        } else { 
            lblTituloLista.setText("  LISTA DE CANASTILLAS");
            lblTituloLista.setBackground(new Color(0, 100, 0)); 
            estilizarTabActivo(lblTabCanastillas); estilizarTabInactivo(lblTabBultos);
            btnAccionPrincipal.setText("FINALIZAR RECEPCIÓN");
            btnAccionPrincipal.setBackground(COLOR_ROJO_OXXO); 
            btnAccionPrincipal.setOpaque(true);
            btnAccionPrincipal.setBorderPainted(false);
        }
        actualizarListaVisual();
        txtCodigoBulto.requestFocus();
    }

    private void escanear() {
        String codigo = txtCodigoBulto.getText().trim();
        if (codigo.isEmpty()) return;
        if (modoEscaneo == 0) procesarBulto(codigo);
        else procesarCanastilla(codigo);
        txtCodigoBulto.setText("");
        txtCodigoBulto.requestFocus();
    }

    private void procesarBulto(String codigo) {
        try {
            boolean esEsperado = baseDeDatos.esBultoEsperado(codigo);
            boolean esValido = maestroProductos.esProductoValido(codigo);
            
            if (esEsperado || esValido) {
                String nombre = esEsperado ? baseDeDatos.getNombreBulto(codigo) : maestroProductos.getNombreProducto(codigo);
                Bulto nuevo = new Bulto(codigo, nombre);
                List<Bulto> lista = esEsperado ? bultosRecibidosOK : bultosSobrantes;
                if (!lista.contains(nuevo)) lista.add(nuevo);
            } else {
                JOptionPane.showMessageDialog(this, "Código Bulto INVÁLIDO", "Error", JOptionPane.ERROR_MESSAGE);
            }
            actualizarListaVisual();
        } catch (Exception e) { }
    }

    private void procesarCanastilla(String codigo) {
        try {
            boolean esEsperada = baseDeDatos.esCanastillaEsperada(codigo);
            boolean esValido = codigo.length() >= 4; 
            
            if (esEsperada || esValido) {
                String tipo = esEsperada ? baseDeDatos.getTipoCanastilla(codigo) : "Canastilla Extra";
                Canastilla nueva = new Canastilla(codigo, tipo);
                List<Canastilla> lista = esEsperada ? canastillasRecibidasOK : canastillasSobrantes;
                if (!lista.contains(nueva)) lista.add(nueva);
            } else {
                JOptionPane.showMessageDialog(this, "Código Canastilla INVÁLIDO", "Error", JOptionPane.ERROR_MESSAGE);
            }
            actualizarListaVisual();
        } catch (Exception e) { }
    }

    private void actualizarListaVisual() {
        areaResumen.setText("");
        if (modoEscaneo == 0) { 
            for(Bulto b : bultosRecibidosOK) areaResumen.append("✓ " + b.getCodigo() + " - " + b.getNombre() + "\n\n");
            for(Bulto b : bultosSobrantes) areaResumen.append("⚠️ " + b.getCodigo() + " - " + b.getNombre() + " [SOBRANTE]\n\n");
        } else { 
            for(Canastilla c : canastillasRecibidasOK) areaResumen.append("✓ " + c.getCodigo() + " - " + c.getTipo() + "\n\n");
            for(Canastilla c : canastillasSobrantes) areaResumen.append("⚠️ " + c.getCodigo() + " - " + c.getTipo() + " [SOBRANTE]\n\n");
        }
    }

    // --- Helpers Visuales ---
    private JPanel crearHeader(String titulo) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_ROJO_OXXO);
        headerPanel.setPreferredSize(new Dimension(420, 50));
        headerPanel.setMaximumSize(new Dimension(420, 50));
        headerPanel.setBorder(new EmptyBorder(0, 15, 0, 15));
        JLabel lblTitle = new JLabel(titulo);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel lblIcons = new JLabel("<html><b>?</b> &nbsp; <b>i</b></html>"); 
        lblIcons.setForeground(Color.WHITE);
        lblIcons.setFont(new Font("Serif", Font.BOLD, 16));
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblIcons, BorderLayout.EAST);
        return headerPanel;
    }

    private JLabel createInfoLabel(String text, boolean bold) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 12));
        lbl.setForeground(COLOR_TEXTO_GRIS);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }
    
    private JLabel createTabLabel(String text, boolean activo) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (activo) estilizarTabActivo(lbl);
        else estilizarTabInactivo(lbl);
        return lbl;
    }
    
    private void estilizarTabActivo(JLabel lbl) {
        lbl.setBackground(COLOR_AZUL_OSCURO);
        lbl.setForeground(Color.WHITE);
        lbl.setBorder(new MatteBorder(0, 0, 3, 0, Color.ORANGE));
    }
    
    private void estilizarTabInactivo(JLabel lbl) {
        lbl.setBackground(new Color(240, 240, 240));
        lbl.setForeground(Color.GRAY);
        lbl.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
    }

    private void estilizarBoton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        // CORRECCIÓN VISUAL: Forzar opacidad y quitar borde pintado
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(250, 45));
        btn.setMaximumSize(new Dimension(250, 45));
    }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new VentanaRecepcion().setVisible(true));
    }
}