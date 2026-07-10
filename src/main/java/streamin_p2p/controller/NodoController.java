package streamin_p2p.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import streamin_p2p.model.Fragmento;
import streamin_p2p.service.NodoService;
import streamin_p2p.service.VideoFragmentService;

@RestController
@RequestMapping("/api/p2p")

public class NodoController {

    private final NodoService nodoService;

    public NodoController(NodoService nodoService){
        this.nodoService=nodoService;
    }

    @GetMapping("/simular-descarga")
    public String simularDescarga(@RequestParam String nodo, @RequestParam int idFragmento){
        //simulamos con bytes de texto
        byte[] contenidoFalso = "pedacito_de_mp4".getBytes();
        Fragmento nuevoFragmento = new Fragmento(idFragmento, contenidoFalso, nodo);

        //registramos el fragmento
        nodoService.registrarFragmento(nodo, nuevoFragmento);

        return "El nodo " + nodo + " aviso a la red que tiene el fragmento " + idFragmento;
    }
    
    //ver el inventario de un nodo
    @GetMapping("/estado")
    public Map<Integer, Fragmento> verEstadoNodo(@RequestParam String nodo){
        return nodoService.obtenerFragmentosDeNodo(nodo);
    }

    @Autowired
    private VideoFragmentService videoFragmentService;

    @PostMapping
    public String inicializarRed(){
        try {
            List<Fragmento> frags = videoFragmentService.dividirVideo();
            for (Fragmento f : frags){
                nodoService.registrarFragmento("NodoA", f);
            }
            return "Video fragmentado y registrado exitosamente en el NodoA";
        } catch (Exception e){
            return "Error al fragmentar video: " + e.getMessage();
        }
    }

    //endpoint para ver todos los fragmentos en la red
    @GetMapping("/red")
    public Map<String, List<Integer>> verEstadoDeLaRed(){
        return nodoService.obtenerEstadoDeLaRed();
    }

    @PostMapping("/solicitar")
    public String solicitarFragmentoManual(@RequestParam String nodoSolicitante, @RequestParam int idFragmento){
        try {
            boolean exito = nodoService.solicitarFragmento(nodoSolicitante, idFragmento);
            if (exito){
                return "Solicitud completada. El " + nodoSolicitante + " ahora tiene el fragmento " + idFragmento;
            } else {
                return "Error: ningun nodo en la red tiene el fragmento " + idFragmento + "todavia";
            }
        } catch (Exception e){
            return "Error en la solicitud: " + e.getMessage();
        }
    }
}
