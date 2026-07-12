package streamin_p2p.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import streamin_p2p.model.Fragmento;
import streamin_p2p.model.SolicitudRequest;
import streamin_p2p.service.NodoService;
import streamin_p2p.service.VideoAssemblerService;
import streamin_p2p.service.VideoFragmentService;

@RestController
@RequestMapping("/api/p2p")

public class NodoController {

    @Autowired
    private final NodoService nodoService;
    
    @Autowired
    private VideoFragmentService videoFragmentService;

    @Autowired
    private VideoAssemblerService videoAssemblerService;

    public NodoController(NodoService nodoService){
        this.nodoService=nodoService;
    }

    @GetMapping("/inicializar")
    public String inicializarRed(){
        try {
            String[] nodos = {"NodoA", "NodoB", "NodoC"};

            //distribuimos los fragmentos
            List<Fragmento> frags = videoFragmentService.dividirVideo();
            for(int i=0; i<frags.size(); i++){
                nodoService.registrarFragmento(nodos[i%3], frags.get(i));
            }
            return "Video dividido y distribuido equitativamente entre los nodos de la red";
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
    public String solicitarFragmento(@RequestBody SolicitudRequest request) {
        try {
            boolean exito = nodoService.solicitarFragmento(request.getNodoSolicitante(), request.getIdFragmento());
            return exito ? "Transferencia exitosa" : "Fragmento no encontrado en la red";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }


    /*@GetMapping("/simular-descarga")
    public String simularDescarga(@RequestParam String nodo, @RequestParam int idFragmento){
        //simulamos con bytes de texto
        byte[] contenidoFalso = "pedacito_de_mp4".getBytes();
        Fragmento nuevoFragmento = new Fragmento(idFragmento, contenidoFalso, nodo);

        //registramos el fragmento
        nodoService.registrarFragmento(nodo, nuevoFragmento);

        return "El nodo " + nodo + " aviso a la red que tiene el fragmento " + idFragmento;
    }*/
    
    //ver el inventario de un nodo
    @GetMapping("/estado")
    public Map<Integer, Fragmento> verEstadoNodo(@RequestParam String nodo){
        return nodoService.obtenerFragmentosDeNodo(nodo);
    }

    @GetMapping("/reconstruir")
    public String reconstruirVideo(@RequestParam String nodo){
        try {
            Map<Integer, Fragmento> fragmentos = nodoService.obtenerFragmentosDeNodo(nodo);
            String nombreArchivo = "video_reconstruido_por_" + nodo + ".mp4";
            videoAssemblerService.reconstruirVideo(fragmentos, nombreArchivo);
            return "Video reconstruido por " + nodo + ". Archivo creado: " + nombreArchivo;
        } catch (Exception e){
            return "Error al reconstruir video: " + e.getMessage();
        }
    }
}
