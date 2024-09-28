package laboratorio.pkg4;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class EPSGUI extends JFrame {
    private EPSQueue epsQueue;
    private JLabel lblTurnoActual;
    private JTextArea areaprioritaria;
    private JTextArea areanormal;
    private Timer timer;
    private Paciente pacienteActual;
    private int tiemporestante;
    
    public EPSGUI(){
        epsQueue = new EPSQueue();
        initComponents();
    }
    
    private void initComponents(){
        setTitle("Asignacion de turnos -EPS");
        setLayout(new BorderLayout());
        
        lblTurnoActual =new JLabel("No hay turnos en curso");
        
        areaprioritaria =new JTextArea(10,20);
        areaprioritaria.setEditable(false);
        areanormal =new JTextArea(10,20);
        areanormal.setEditable(false);
        
        JScrollPane scrollprioritarios = new JScrollPane(areaprioritaria);
        JScrollPane scrollnormal = new JScrollPane(areanormal);
        
        JPanel panelcolas = new JPanel(new GridLayout(1,2));
        panelcolas.add(new JLabel("pacientes prioritarios"));
        panelcolas.add(new JLabel("pacientes normales"));
        panelcolas.add(scrollprioritarios);
        panelcolas.add(scrollnormal);
        
        JButton btnAgregarpaciente = new JButton("Agregar Paciente");
        btnAgregarpaciente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarpaciente();
            }
        });
        
        JButton btnExtenderTiempo = new JButton("Extender Tiempo");
        btnExtenderTiempo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                extenderTiempo();
            }
        });
        
        JPanel panelTurno = new JPanel(new GridLayout(3,1));
        panelTurno.add(lblTurnoActual);
        panelTurno.add(btnExtenderTiempo);
        
        add(panelTurno, BorderLayout.NORTH);
        add(panelcolas, BorderLayout.CENTER);
        add(btnAgregarpaciente, BorderLayout.SOUTH);
        
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    private void agregarpaciente(){
        String nombre =JOptionPane.showInputDialog("Nombre y Apellidos:");
        int edad = Integer.parseInt(JOptionPane.showInputDialog("Edad:"));
        String afiliacion = JOptionPane.showInputDialog("Afiliación (POS/PC):");
        boolean embarazada = JOptionPane.showConfirmDialog(null, "¿Está embarazada?") == JOptionPane.YES_OPTION;
        boolean limitacionMotriz = JOptionPane.showConfirmDialog(null, "¿Tiene limitación motriz?") == JOptionPane.YES_OPTION;
    
        Paciente nuevopaciente = new Paciente(nombre, edad, afiliacion,embarazada,limitacionMotriz);
        epsQueue.agregarPaciente(nuevopaciente);
        
        actualizarListasPacientes();

        if (pacienteActual == null) {
            iniciarSimulacion();
    }
}


 void iniciarSimulacion() {
        if (epsQueue.hayPacientes()) {
            pacienteActual = epsQueue.siguientePaciente();
            lblTurnoActual.setText("Turno en curso: " + pacienteActual.getNombre());
            tiemporestante = 5;

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    tiemporestante--;
                    if (tiemporestante <= 0) {
                        siguienteTurno();
                    } else {
                        lblTurnoActual.setText("Turno en curso: " + pacienteActual.getNombre() + " - Tiempo restante: " + tiemporestante);
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
            tiemporestante += 5;
        }
    }

    private void actualizarListasPacientes() {
        areaprioritaria.setText("Pacientes Prioritarios:\n");
        areanormal.setText("Pacientes Normales:\n");

        for (Paciente p : epsQueue.getPacientesPrioritarios()) {
            areaprioritaria.append(p.getNombre() + "\n");
        }

        for (Paciente p : epsQueue.getPacientesNormales()) {
            areanormal.append(p.getNombre() + "\n");
        }
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
        return colaNormal.isEmpty() ? null : colaNormal.poll();
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
