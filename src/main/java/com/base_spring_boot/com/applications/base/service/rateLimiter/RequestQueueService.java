package com.base_spring_boot.com.applications.base.service.rateLimiter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class RequestQueueService {
    private final BlockingQueue<HttpServletRequest> requestQueue;

    public RequestQueueService() {
        this.requestQueue = new ArrayBlockingQueue<>(74);  // Taille maximale de la file d'attente
    }

    public void addRequestToQueue(HttpServletRequest request) throws InterruptedException {
        requestQueue.put(request);  // Attente si la queue est pleine
    }

    public HttpServletRequest getNextRequestFromQueue() throws InterruptedException {
        return requestQueue.take();  // Prend la prochaine requête
    }
}
