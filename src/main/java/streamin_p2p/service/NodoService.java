package streamin_p2p.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import streamin_p2p.event.FragmentoDisponibleEvent;
import streamin_p2p.model.Fragmento;

@Service
public class NodoService {
    private final ApplicationEventPublisher eventPublisher; //almacenamiento de fragmentos

    //lista de nodos
    private final Map<String, Map<Integer, Fragmento>> storage = new HashMap<>();

    private final List<String> nodosEnLaRed = Arrays.asList("NodoA", "NodoB", "NodoC");

    public NodoService(ApplicationEventPublisher eventPublisher){
        this.eventPublisher = eventPublisher;
        //nodos con mapas vacios
        for (String nodo : nodosEnLaRed){
            storage.put(nodo, new HashMap<>());
        }
    }

    public void registrarFragmento(String nodoId, Fragmento frag){
        storage.get(nodoId).put(frag.getId(), frag);
        System.out.println(nodoId + " descargo el fragmento " + frag.getId());

        //nodo le avisa a todos los demas
        eventPublisher.publishEvent(new FragmentoDisponibleEvent(this, frag));
    }

    @EventListener
    //avisa cuando un nodo consigue un fragmento
    public void escucharRed(FragmentoDisponibleEvent event){
        Fragmento fragAnunciado = event.getFragmento();
        String nodoQueLoTiene = fragAnunciado.getNodoOrigen();
        int idDelFragmento = fragAnunciado.getId();

        System.out.println("[Alerta P2P] " + nodoQueLoTiene + " anuncio que tiene el fragmento " + idDelFragmento);

        //los demas nodos revisan si les falta el fragmento
        for (String otroNodo : nodosEnLaRed){
            if (!otroNodo.equals(nodoQueLoTiene)){
                //si no esta el fragmento en el nodo, se lo descarga
                if (!storage.get(otroNodo).containsKey(idDelFragmento)){
                    System.out.println(otroNodo + " anuncia que le falta el fragmento " + idDelFragmento + ", solicitando descarga desde " + nodoQueLoTiene);

                    //simulacion de la descarga
                    Fragmento copiaDescarga = new Fragmento(idDelFragmento, fragAnunciado.getContenido(), otroNodo);
                    storage.get(otroNodo).put(idDelFragmento, copiaDescarga);
                    System.out.println(otroNodo + " termino de descargar el fragmento " + idDelFragmento);
                }
            }
        }
    }
}
