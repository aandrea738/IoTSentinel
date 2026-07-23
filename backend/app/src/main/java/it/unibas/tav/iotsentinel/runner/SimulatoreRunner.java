package it.unibas.tav.iotsentinel.runner;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import it.unibas.tav.iotsentinel.modello.misurazione.ETipoMisurazione;

@QuarkusMain(name = "simulatore")
public class SimulatoreRunner implements QuarkusApplication {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Random random = new Random();
    private final AtomicBoolean running = new AtomicBoolean(true);

    @ConfigProperty(name = "iotsentinel.simulatore.ingestione-url")
    String ingestioneUrl;

    @Override
    public int run(String... args) throws Exception {
        int threads = 2;
        double errorRate = 0.1;
        long delayMs = 500;

        if (args.length == 1 && args[0].contains(" ")) {
            args = args[0].split("\\s+");
        }

        for (String arg : args) {
            if (arg.startsWith("--threads=")) {
                threads = Integer.parseInt(arg.substring(10));
            } else if (arg.startsWith("--error-rate=")) {
                errorRate = Double.parseDouble(arg.substring(13));
            } else if (arg.startsWith("--delay=")) {
                delayMs = Long.parseLong(arg.substring(8));
            }
        }

        System.out.println("Avvio Simulatore IoT Sentinel");
        System.out.println("Threads: " + threads);
        System.out.println("Error Rate: " + (errorRate * 100) + "%");
        System.out.println("Delay: " + delayMs + "ms");
        System.out.println("Premi Ctrl+C per fermare...");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running.set(false);
            System.out.println("Arresto in corso...");
        }));

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        final double finalErrorRate = errorRate;
        final long finalDelayMs = delayMs;

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> runWorker(finalErrorRate, finalDelayMs));
        }

        while (running.get()) {
            Thread.sleep(1000);
        }

        executor.shutdownNow();
        return 0;
    }

    private void runWorker(double errorRate, long delayMs) {
        ETipoMisurazione[] tipi = ETipoMisurazione.values();

        while (running.get()) {
            try {
                // Scegli sensore a caso garantendo la coerenza del tipo
                int idSensore;
                ETipoMisurazione tipo;

                int rnd = random.nextInt(3);
                if (rnd == 0) {
                    idSensore = random.nextBoolean() ? 1 : 2;
                    tipo = ETipoMisurazione.TEMPERATURA;
                } else if (rnd == 1) {
                    idSensore = random.nextBoolean() ? 3 : 4;
                    tipo = ETipoMisurazione.PRESSIONE;
                } else {
                    idSensore = random.nextBoolean() ? 7 : 8;
                    tipo = ETipoMisurazione.CO2;
                }

                boolean isError = random.nextDouble() < errorRate;
                double valore = generaValore(tipo, isError);

                String payload = String.format("""
                        {
                            "idSensore": %d,
                            "valore": %s,
                            "tipoMisurazione": "%s",
                            "timestamp": "%s"
                        }""",
                        idSensore, String.valueOf(valore), tipo.name(), Instant.now().toString());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ingestioneUrl))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(payload))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200 || response.statusCode() == 201 || response.statusCode() == 204) {
                    System.out.printf("[%s] Inviato sensore %d (%s) - Valore: %.2f%n",
                            Thread.currentThread().getName(), idSensore, tipo.name(), valore);
                } else {
                    System.out.printf("[%s] Errore HTTP %d: %s%n",
                            Thread.currentThread().getName(), response.statusCode(), response.body());
                }

                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.out.printf("[%s] Errore: %s%n", Thread.currentThread().getName(), e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private double generaValore(ETipoMisurazione tipo, boolean isError) {
        return switch (tipo) {
            case TEMPERATURA -> isError ? 80 + random.nextDouble() * 20 : 20 + random.nextDouble() * 10;
            case PRESSIONE -> isError ? 5 + random.nextDouble() * 5 : 1 + random.nextDouble() * 1;
            case CO2 -> isError ? 1000 + random.nextDouble() * 500 : 400 + random.nextDouble() * 200;
        };
    }
}
