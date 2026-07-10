package streamin_p2p.service;

import java.util.ArrayList;
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

    //consulta que tiene un nodo especifico
    public Map<Integer, Fragmento> obtenerFragmentosDeNodo(String nodoId){
        if (!storage.containsKey(nodoId)){
            throw new IllegalArgumentException ("El nodo " + nodoId + " no existe en la red.");
        }
        return storage.get(nodoId);
    }

    public Map<String, List<Integer>> obtenerEstadoDeLaRed(){
        Map<String, List<Integer>> estadoRed = new HashMap<>();

        for(String nodo : nodosEnLaRed){
            Map<Integer, Fragmento> fragmentosDelNodo = storage.get(nodo);
            List<Integer> ids = new ArrayList<>(fragmentosDelNodo.keySet());
            estadoRed.put(nodo, ids);
        }
        return estadoRed;
    }

    public boolean solicitarFragmento(String nodoSolicitante, int idFragmento) {
        // validar que el nodo solicitante exista
        if (!storage.containsKey(nodoSolicitante)) {
            System.out.println("Nodo solicitante no encontrado: " + nodoSolicitante);
            return false;
        }

        // validar que el inventario del nodo no sea null
        Map<Integer, Fragmento> inventarioNodo = storage.get(nodoSolicitante);
        if (inventarioNodo == null) {
            return false;
        }

        
        if (inventarioNodo.containsKey(idFragmento)) {
            System.out.println("El nodo " + nodoSolicitante + " ya tiene el fragmento " + idFragmento);
            return true;
        }

        System.out.println(nodoSolicitante + " buscando el fragmento " + idFragmento + " en la red...");

        // buscar en los otros nodos
        for (String posibleProveedor : nodosEnLaRed) {
            if (!posibleProveedor.equals(nodoSolicitante)) {
                Map<Integer, Fragmento> inventarioProveedor = storage.get(posibleProveedor);
                
                //validación de seguridad aquí
                if (inventarioProveedor != null && inventarioProveedor.containsKey(idFragmento)) {
                    Fragmento original = inventarioProveedor.get(idFragmento);
                    Fragmento copia = new Fragmento(idFragmento, original.getContenido(), nodoSolicitante);
                    
                    storage.get(nodoSolicitante).put(idFragmento, copia);
                    System.out.println("   -> Transferencia P2P exitosa de " + posibleProveedor + " a " + nodoSolicitante);
                    return true;
                }
            }
        }
        return false;
    }
}
