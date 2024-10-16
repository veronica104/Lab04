package laboratorio.pkg4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class EPSGUI extends JFrame {
    private EPSQueue epsQueue;
    private JLabel lblTurnoActual;
    private JTextArea areaPacientesPrioritarios;
    private JTextArea areaPacientesNormales;
    private Timer timer;
    private Paciente pacienteActual;
    private int tiempoRestante;

    public EPSGUI() {
        epsQueue = new EPSQueue();
        initComponents();
    }

    private void initComponents() {
        setTitle("Asignación de Turnos - EPS");
        setLayout(new BorderLayout());

        lblTurnoActual = new JLabel("No hay turnos en curso");

        // Crear las áreas de texto para mostrar las colas de pacientes
        areaPacientesPrioritarios = new JTextArea(10, 20);
        areaPacientesPrioritarios.setEditable(false);
        areaPacientesNormales = new JTextArea(10, 20);
        areaPacientesNormales.setEditable(false);
        
        JScrollPane scrollPrioritarios = new JScrollPane(areaPacientesPrioritarios);
        JScrollPane scrollNormales = new JScrollPane(areaPacientesNormales);
        
        JPanel panelColas = new JPanel(new GridLayout(1, 2));
        panelColas.add(new JLabel("Pacientes Prioritarios"));
        panelColas.add(new JLabel("Pacientes Normales"));
        panelColas.add(scrollPrioritarios);
        panelColas.add(scrollNormales);

        JButton btnAgregarPaciente = new JButton("Agregar Paciente");
        btnAgregarPaciente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarPaciente();
            }
        });

        JButton btnExtenderTiempo = new JButton("Extender Tiempo");
        btnExtenderTiempo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                extenderTiempo();
            }
        });

        JPanel panelTurno = new JPanel(new GridLayout(3, 1));
        panelTurno.add(lblTurnoActual);
        panelTurno.add(btnExtenderTiempo);

        add(panelTurno, BorderLayout.NORTH);
        add(panelColas, BorderLayout.CENTER);
        add(btnAgregarPaciente, BorderLayout.SOUTH);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void agregarPaciente() {
        String nombre = JOptionPane.showInputDialog("Nombre y Apellidos:");
        int edad = Integer.parseInt(JOptionPane.showInputDialog("Edad:"));
        String afiliacion = JOptionPane.showInputDialog("Afiliación (POS/PC):");
        boolean embarazada = JOptionPane.showConfirmDialog(null, "¿Está embarazada?") == JOptionPane.YES_OPTION;
        boolean limitacionMotriz = JOptionPane.showConfirmDialog(null, "¿Tiene limitación motriz?") == JOptionPane.YES_OPTION;

        Paciente nuevoPaciente = new Paciente(nombre, edad, afiliacion, embarazada, limitacionMotriz);
        epsQueue.agregarPaciente(nuevoPaciente);

        actualizarListasPacientes();

        if (pacienteActual == null) {
            iniciarSimulacion();
        }
    }

    private void iniciarSimulacion() {
        if (epsQueue.hayPacientes()) {
            pacienteActual = epsQueue.siguientePaciente();
            lblTurnoActual.setText("Turno en curso: " + pacienteActual.getNombre());
            tiempoRestante = 5;

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    tiempoRestante--;
                    if (tiempoRestante <= 0) {
                        siguienteTurno();
                    } else {
                        lblTurnoActual.setText("Turno en curso: " + pacienteActual.getNombre() + " - Tiempo restante: " + tiempoRestante);
                    }
                }
            }, 0, 1000);
        }
    }

    private void siguienteTurno() {
        timer.cancel();
        pacienteActual = null;
        lblTurnoActual.setText("No hay turnos en curso");

        if (epsQueue.hayPacientes()) {
            iniciarSimulacion();
        }
        actualizarListasPacientes();
    }

    private void extenderTiempo() {
        if (pacienteActual != null) {
            tiempoRestante += 5;
        }
    }

    private void actualizarListasPacientes() {
        areaPacientesPrioritarios.setText("Pacientes Prioritarios:\n");
        areaPacientesNormales.setText("Pacientes Normales:\n");

        for (Paciente p : epsQueue.getPacientesPrioritarios()) {
            areaPacientesPrioritarios.append(p.getNombre() + "\n");
        }

        for (Paciente p : epsQueue.getPacientesNormales()) {
            areaPacientesNormales.append(p.getNombre() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EPSGUI());
    }
}

class EPSQueue {
    private Queue<Paciente> colaNormal;
    private Queue<Paciente> colaPrioritaria;

    public EPSQueue() {
        colaNormal = new LinkedList<>();
        colaPrioritaria = new LinkedList<>();
    }

    public void agregarPaciente(Paciente paciente) {
        if (paciente.tienePrioridad()) {
            colaPrioritaria.add(paciente);
        } else {
            colaNormal.add(paciente);
        }
    }

    public Paciente siguientePaciente() {
        if (!colaPrioritaria.isEmpty()) {
            return colaPrioritaria.poll();
        }
        return colaNormal.poll();
    }

    public boolean hayPacientes() {
        return !colaPrioritaria.isEmpty() || !colaNormal.isEmpty();
    }

    public int totalPacientes() {
        return colaPrioritaria.size() + colaNormal.size();
    }

    public Queue<Paciente> getPacientesPrioritarios() {
        return colaPrioritaria;
    }

    public Queue<Paciente> getPacientesNormales() {
        return colaNormal;
    }
}

class Paciente {
    private String nombre;
    private int edad;
    private String afiliacion;
    private boolean embarazada;
    private boolean limitacionMotriz;
    private boolean prioridad;

    public Paciente(String nombre, int edad, String afiliacion, boolean embarazada, boolean limitacionMotriz) {
        this.nombre = nombre;
        this.edad = edad;
        this.afiliacion = afiliacion;
        this.embarazada = embarazada;
        this.limitacionMotriz = limitacionMotriz;
        this.prioridad = calcularPrioridad();
    }

    private boolean calcularPrioridad() {
        return (edad >= 60 || edad <= 12 || embarazada || limitacionMotriz || afiliacion.equals("PC"));
    }

    public boolean tienePrioridad() {
        return prioridad;
    }

    public String getNombre() {
        return nombre;
    }
}
