package br.com.controle.financeiro.controller.api;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import br.com.controle.financeiro.controller.api.linkbuilder.InstitutionDTOResourceAssembler;
import br.com.controle.financeiro.model.dto.InstitutionDTO;
import br.com.controle.financeiro.model.entity.Institution;
import br.com.controle.financeiro.model.exception.InstitutionNotFoundException;
import br.com.controle.financeiro.model.repository.InstitutionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/institution", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InstitutionController {

    private static final Logger LOG = LoggerFactory.getLogger(InstitutionController.class);

    private final InstitutionRepository institutionRepository;

    private final InstitutionDTOResourceAssembler institutionDTOResourceAssembler;

    public InstitutionController(InstitutionRepository institutionRepository,
                                 InstitutionDTOResourceAssembler institutionDTOResourceAssembler) {
        this.institutionRepository = institutionRepository;
        this.institutionDTOResourceAssembler = institutionDTOResourceAssembler;
    }

    @GetMapping
    public Resources<Resource<InstitutionDTO>> allInstitutions() {
        LOG.debug("finding allInstitutions");

        final List<Resource<InstitutionDTO>> institutions =
                institutionRepository.findAll().stream().map(InstitutionDTO::fromInstitution)
                                     .map(institutionDTOResourceAssembler::toResource).collect(Collectors.toList());

        return new Resources<>(institutions,
                               linkTo(methodOn(InstitutionController.class).allInstitutions()).withSelfRel());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public Resource<InstitutionDTO> newInstitution(@RequestBody @Valid final InstitutionDTO institution) {
        LOG.debug("creating newInstitution");
        InstitutionDTO savedInstitution =
                InstitutionDTO.fromInstitution(institutionRepository.save(institution.toInstitution()));
        return institutionDTOResourceAssembler.toResource(savedInstitution);
    }

    @GetMapping(path = "/{id}")
    public Resource<InstitutionDTO> oneInstitution(@PathVariable(value = "id") final UUID id) {
        LOG.debug("searching oneInstitution ${}", id);
        final Institution institution =
                institutionRepository.findById(id).orElseThrow(() -> new InstitutionNotFoundException(id));
        return institutionDTOResourceAssembler.toResource(InstitutionDTO.fromInstitution(institution));
    }

    @PutMapping(path = "/{id}")
    public Resource<InstitutionDTO> replaceInstitution(@RequestBody final InstitutionDTO newInstitution,
                                                       @PathVariable final UUID id) {
        LOG.info("replaceInstitution");
        //TODO verify DTO integrity
        Institution savedInstitution = institutionRepository.findById(id).map(inst -> {
            inst.setName(newInstitution.getName());
            return institutionRepository.save(inst);
        }).orElseGet(() -> {
            newInstitution.setId(id);
            return institutionRepository.save(newInstitution.toInstitution());
        });

        return institutionDTOResourceAssembler.toResource(InstitutionDTO.fromInstitution(savedInstitution));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{id}")
    public void deleteInstitution(@PathVariable final UUID id) {
        LOG.debug("trying to deleteInstitution ${}", id);
        //TODO verify authenticated permission
        institutionRepository.deleteById(id);
    }

}
