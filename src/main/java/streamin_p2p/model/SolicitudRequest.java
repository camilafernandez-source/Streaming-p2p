package streamin_p2p.model;

import lombok.Data;

@Data
public class SolicitudRequest {
    private String nodoSolicitante;
    private int idFragmento;
}
