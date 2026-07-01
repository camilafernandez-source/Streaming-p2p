package streamin_p2p.service;

import streamin_p2p.model.Fragmento;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import streamin_p2p.event.FragmentoDisponibleEvent;

@Service
public class NodoService {
    private final ApplicationEventPublisher eventPublisher; //almacenamiento de fragmentos

    private final Map<String, Map<Integer, Fragmento>> storage = new HashMap<>();

    public NodoService(ApplicationEventPublisher eventPublisher){
        this.eventPublisher = eventPublisher;
    }

    public void registrarFragmento(String nodoId, Fragmento frag){
        storage.computeIfAbsent(nodoId, k -> new HashMap<>()).put(frag.getId(), frag);
        System.out.println("Nodo " + nodoId + "adquirio el fragmento" + frag.getId());

        // Publicar el evento de fragmento disponible
        eventPublisher.publishEvent(new FragmentoDisponibleEvent(this, frag));
    }
}
