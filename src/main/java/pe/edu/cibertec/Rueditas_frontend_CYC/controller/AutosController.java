package pe.edu.cibertec.Rueditas_frontend_CYC.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import pe.edu.cibertec.Rueditas_frontend_CYC.dto.AutosRequestDTO;
import pe.edu.cibertec.Rueditas_frontend_CYC.dto.AutosResponseDTO;
import pe.edu.cibertec.Rueditas_frontend_CYC.viewmodel.AutosModel;

@Controller
@RequestMapping("/autos")
public class AutosController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/inicio")
    public String busqueda(Model model) {
        AutosModel autosModel = new AutosModel("00", "", "");
        model.addAttribute("autosModel", autosModel);
        return "inicio";
    }

    @PostMapping("/buscarAuto")
    public String autenticar(@RequestParam("placa") String placa, Model model) {
        if (placa == null || placa.trim().length() == 0 || !placa.matches("^[A-Za-z]{3}-[0-9]{4}$")) {
            AutosModel autosModel = new AutosModel("01", "Error: Debe ingresar una placa correcta, inténtelo nuevamente", "");
            model.addAttribute("autosModel", autosModel);
            return "inicio";
        }

        try {

            String endpoint = "http://localhost:8081/informacionauto/busqueda";
            AutosRequestDTO autosRequestDTO = new AutosRequestDTO(placa);
            AutosResponseDTO autosResponseDTO = restTemplate.postForObject(endpoint, autosRequestDTO, AutosResponseDTO.class);

            if (autosResponseDTO.codigo().equals("00")) {
                AutosModel autosModel = new AutosModel("00", "", autosRequestDTO.placa());
                model.addAttribute("autosModel", autosModel);
                model.addAttribute("autosResponseDTO", autosResponseDTO);
                return "principal";
            } else {
                AutosModel autosModel = new AutosModel("01", "No se encuentran los datos para la placa consultada", "");
                model.addAttribute("autosModel", autosModel);
                System.out.println("Request DTO enviado: " + autosRequestDTO.placa());
                return "inicio";
            }
        } catch (Exception e) {
            AutosModel autosModel = new AutosModel("02", "Error: Ocurrió un problema al realizar la búsqueda", "");
            model.addAttribute("autosModel", autosModel);
            return "inicio";
        }


    }
}
