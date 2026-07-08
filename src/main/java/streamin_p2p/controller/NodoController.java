package streamin_p2p.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import streamin_p2p.model.Fragmento;
import streamin_p2p.service.NodoService;

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
}
