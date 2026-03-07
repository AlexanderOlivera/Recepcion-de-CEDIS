import java.util.Scanner;
import java.lang.Math;

public class Bombe {

    // Función auxiliar para obtener valor numérico (A=1, B=2...)
    public static double getVal(char c) {
        return (double) (c - 'A' + 1);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Entrada de datos
        System.out.println("Escribe el texto cifrado:");
        String textoCifrado = scanner.nextLine().toUpperCase();

        System.out.println("Escribe el significado esperado (Crib):");
        String crib = scanner.nextLine().toUpperCase();

        // Configuración inicial de los rotores (A, A, A, A, A)
        char[] rotoresInit = {'A', 'A', 'A', 'A', 'A'};
        boolean encontrado = false;

        System.out.println("\nBuscando combinación...");

        // 2. Bucle Principal: Prueba todas las configuraciones iniciales
        while (!encontrado) {
            // Convertimos a array de chars para poder modificar letra por letra
            char[] textoTemp = textoCifrado.toCharArray();
            
            // Copia de los rotores para esta iteración
            char[] r = rotoresInit.clone();

            // Procesar cada letra del texto
            for (int i = 0; i < textoTemp.length; i++) {
                // Ignorar caracteres que no sean letras A-Z
                if (textoTemp[i] < 'A' || textoTemp[i] > 'Z') continue;

                // Valores numéricos actuales de los rotores
                double[] vals = new double[5];
                for (int k = 0; k < 5; k++) vals[k] = getVal(r[k]);

                // Lógica de "Ohm" (base según posición de rotores)
                double ohm = 30;
                if (r[4] != 'A') ohm = 300;
                else if (r[3] != 'A') ohm = 240;
                else if (r[2] != 'A') ohm = 180;
                else if (r[1] != 'A') ohm = 120;
                else if (r[0] != 'A') ohm = 60;

                // FÓRMULA MATEMÁTICA (Traducción exacta de tu lógica)
                // vals[0]=alfa, vals[1]=beta, vals[2]=teta, vals[3]=ro, vals[4]=epsilon
                double term1 = Math.pow(vals[0] + vals[1] + 1, 2);
                double term2 = (vals[0] + vals[1] + vals[2] + 1) * (vals[0] + vals[1] + vals[2] + vals[3] + 1);
                double term3 = (vals[1] + 1) * (vals[1] + vals[2] + 1);

                double ohmega = Math.abs(
                    term1 - term2 + term3
                    - ((vals[1] + vals[2] + vals[3] + 1) * (vals[1] + vals[2] + vals[3] + vals[4] + 1))
                    + ((vals[2] + 1) * (vals[2] + vals[0] + vals[3] + 1))
                    - ((vals[2] + vals[0] + vals[3] + vals[4] + 1) * (vals[0] + vals[2] + vals[3] + vals[4] + 1))
                    + ((vals[3] + 1) * (vals[3] + vals[0] + 1))
                    - ((vals[3] + vals[0] + vals[1] + 1) * (vals[3] + vals[0] + vals[1] + vals[4] + 1))
                    + ((vals[2] + 1) * (vals[2] + vals[0] + 1))
                    - ((vals[2] + vals[0] + vals[1] + 1) * (vals[2] + vals[0] + vals[1] + vals[4] + 1))
                ) + ohm;

                // Ajuste de rangos
                if (ohmega >= 3000 && ohmega < 5000) ohmega -= 1000;
                else if (ohmega >= 5000 && ohmega < 8000) ohmega -= 3000;
                else if (ohmega >= 8000) ohmega -= 5000;

                // Desplazamiento inverso (Descifrado)
                int shift = (int) ohmega;
                for (int s = 0; s < shift; s++) {
                    if (textoTemp[i] <= 'A') textoTemp[i] = 'Z';
                    else textoTemp[i]--;
                }

                // Paso de Rotores (Odómetro interno)
                r[0]++;
                if (r[0] > 'Z') { r[0] = 'A'; r[1]++;
                    if (r[1] > 'Z') { r[1] = 'A'; r[2]++;
                        if (r[2] > 'Z') { r[2] = 'A'; r[3]++;
                            if (r[3] > 'Z') { r[3] = 'A'; r[4]++;
                                if (r[4] > 'Z') r[4] = 'A';
                            }
                        }
                    }
                }
            }

            // 3. Verificación
            String resultado = new String(textoTemp);
            
            if (resultado.equals(crib)) {
                System.out.println("\n\n*** COMBINACIÓN ENCONTRADA ***");
                System.out.println("Configuración: " + rotoresInit[4] + " " + rotoresInit[3] + " " + rotoresInit[2] + " " + rotoresInit[1] + " " + rotoresInit[0]);
                System.out.println("Texto descifrado: " + resultado);
                encontrado = true;
            }

            // Incrementar configuración INICIAL para la siguiente vuelta (Odómetro externo)
            rotoresInit[0]++;
            if (rotoresInit[0] > 'Z') { rotoresInit[0] = 'A'; rotoresInit[1]++;
                if (rotoresInit[1] > 'Z') { rotoresInit[1] = 'A'; rotoresInit[2]++;
                    if (rotoresInit[2] > 'Z') { rotoresInit[2] = 'A'; rotoresInit[3]++;
                        if (rotoresInit[3] > 'Z') { rotoresInit[3] = 'A'; rotoresInit[4]++;
                            if (rotoresInit[4] > 'Z') break; // Fin de todas las combinaciones
                        }
                    }
                }
            }
        }
        scanner.close();
    }
}