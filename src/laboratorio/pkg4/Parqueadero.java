package laboratorio.pkg4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Stack;

class Vehiculo {
    String placa;
    String tipo;
    String horaIngreso;
    int numero;
    double valorPagar;

    public Vehiculo(String placa, String tipo, String horaIngreso, int numero) {
        this.placa = placa;
        this.tipo = tipo;
        this.horaIngreso = horaIngreso;
        this.numero = numero;
        this.valorPagar = 0.0;
    }

    public void calcularValorPagar(int minutos) {
        if (tipo.equals("Bicicleta") || tipo.equals("Ciclomotor")) {
            valorPagar = minutos * 20;
        } else if (tipo.equals("Motocicleta")) {
            valorPagar = minutos * 30;
        } else if (tipo.equals("Carro")) {
            valorPagar = minutos * 60;
        }
    }

    @Override
    public String toString() {
        return "Vehiculo #" + numero + ": Placa=" + placa + ", Tipo=" + tipo + ", Hora de Ingreso=" + horaIngreso + ", Valor a pagar: $" + valorPagar + " COP";
    }
}

public class Parqueadero extends JFrame {
    private ArrayList<Vehiculo> vehiculos = new ArrayList<>();
    private Stack<Vehiculo> vehiculos2Ruedas = new Stack<>();
    private Stack<Vehiculo> vehiculos4Ruedas = new Stack<>();
    private int contadorVehiculos = 0;

    public Parqueadero() {
        setTitle("Administración de Parqueadero");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Crear el menú
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Opciones");
        JMenuItem ingresoItem = new JMenuItem("1. Ingreso de Vehículo");
        JMenuItem visualizarItem = new JMenuItem("2. Visualizar Tabla Actualizada");
        JMenuItem dosRuedasItem = new JMenuItem("3. Visualizar Vehículos de 2 Ruedas");
        JMenuItem cuatroRuedasItem = new JMenuItem("4. Visualizar Vehículos de 4 Ruedas");
        JMenuItem totalVehiculosItem = new JMenuItem("5. Cantidad de Vehículos y Valor Total");
        JMenuItem eliminarItem = new JMenuItem("6. Eliminar Vehículo");
        JMenuItem salirItem = new JMenuItem("7. Salir");

        // Agregar los items al menú
        menu.add(ingresoItem);
        menu.add(visualizarItem);
        menu.add(dosRuedasItem);
        menu.add(cuatroRuedasItem);
        menu.add(totalVehiculosItem);
        menu.add(eliminarItem);
        menu.add(salirItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Panel de resultados
        JTextArea resultArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.CENTER);

        // Acciones de los menús
        ingresoItem.addActionListener(e -> ingresarVehiculo());
        visualizarItem.addActionListener(e -> visualizarTabla(resultArea));
        dosRuedasItem.addActionListener(e -> visualizar2Ruedas(resultArea));
        cuatroRuedasItem.addActionListener(e -> visualizar4Ruedas(resultArea));
        totalVehiculosItem.addActionListener(e -> cantidadYTotal(resultArea));
        eliminarItem.addActionListener(e -> eliminarVehiculo(resultArea));
        salirItem.addActionListener(e -> System.exit(0));
    }

    private void ingresarVehiculo() {
        String placa = JOptionPane.showInputDialog("Ingrese la placa del vehículo:");
        String[] tipos = {"Bicicleta", "Ciclomotor", "Motocicleta", "Carro"};
        String tipo = (String) JOptionPane.showInputDialog(null, "Seleccione el tipo de vehículo", "Tipo de Vehículo", JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);
        String horaIngreso = JOptionPane.showInputDialog("Ingrese la hora de ingreso (HH:MM):");
        contadorVehiculos++;

        Vehiculo vehiculo = new Vehiculo(placa, tipo, horaIngreso, contadorVehiculos);
        vehiculos.add(vehiculo);

        if (tipo.equals("Bicicleta") || tipo.equals("Ciclomotor") || tipo.equals("Motocicleta")) {
            vehiculos2Ruedas.push(vehiculo);
        } else if (tipo.equals("Carro")) {
            vehiculos4Ruedas.push(vehiculo);
        }

        JOptionPane.showMessageDialog(null, "Vehículo ingresado exitosamente.");
    }

    private void visualizarTabla(JTextArea resultArea) {
        resultArea.setText("Tabla de Vehículos:\n");
        for (Vehiculo v : vehiculos) {
            v.calcularValorPagar(60);  // Suponiendo 60 minutos como ejemplo
            resultArea.append(v.toString() + "\n");
        }
    }

    private void visualizar2Ruedas(JTextArea resultArea) {
        resultArea.setText("Vehículos de 2 Ruedas:\n");
        for (Vehiculo v : vehiculos2Ruedas) {
            v.calcularValorPagar(60);  // Suponiendo 60 minutos como ejemplo
            resultArea.append(v.toString() + "\n");
        }
    }

    private void visualizar4Ruedas(JTextArea resultArea) {
        resultArea.setText("Vehículos de 4 Ruedas:\n");
        for (Vehiculo v : vehiculos4Ruedas) {
            v.calcularValorPagar(60);  // Suponiendo 60 minutos como ejemplo
            resultArea.append(v.toString() + "\n");
        }
    }

    private void cantidadYTotal(JTextArea resultArea) {
        int totalVehiculos = vehiculos.size();
        double totalValor = 0;
        for (Vehiculo v : vehiculos) {
            v.calcularValorPagar(60);  // Suponiendo 60 minutos como ejemplo
            totalValor += v.valorPagar;
        }
        resultArea.setText("Total de Vehículos: " + totalVehiculos + "\n");
        resultArea.append("Valor Total a Pagar: $" + totalValor + " COP\n");
    }

    private void eliminarVehiculo(JTextArea resultArea) {
        String placa = JOptionPane.showInputDialog("Ingrese la placa del vehículo a eliminar:");
        Vehiculo vehiculoAEliminar = null;
        for (Vehiculo v : vehiculos) {
            if (v.placa.equals(placa)) {
                vehiculoAEliminar = v;
                break;
            }
        }

        if (vehiculoAEliminar != null) {
            vehiculos.remove(vehiculoAEliminar);
            if (vehiculoAEliminar.tipo.equals("Bicicleta") || vehiculoAEliminar.tipo.equals("Ciclomotor") || vehiculoAEliminar.tipo.equals("Motocicleta")) {
                vehiculos2Ruedas.remove(vehiculoAEliminar);
            } else if (vehiculoAEliminar.tipo.equals("Carro")) {
                vehiculos4Ruedas.remove(vehiculoAEliminar);
            }
            resultArea.setText("Vehículo con placa " + placa + " eliminado.");
        } else {
            JOptionPane.showMessageDialog(null, "Vehículo no encontrado.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Parqueadero().setVisible(true);
        });
    }
}

