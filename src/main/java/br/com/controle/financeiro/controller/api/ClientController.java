package br.com.controle.financeiro.controller.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import br.com.controle.financeiro.controller.api.linkbuilder.ClientDTOResourceAssembler;
import br.com.controle.financeiro.model.dto.ClientDTO;
import br.com.controle.financeiro.model.entity.Client;
import br.com.controle.financeiro.model.entity.UserEntity;
import br.com.controle.financeiro.model.exception.ClientNotFoundException;
import br.com.controle.financeiro.model.repository.ClientRepository;
import br.com.controle.financeiro.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
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
@RequestMapping(value = "/api/client", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {

    private static final Logger LOG = LoggerFactory.getLogger(ClientController.class);

    private final ClientRepository clientRepository;

    private final UserService userService;

    private final ClientDTOResourceAssembler clientDTOResourceAssembler;

    public ClientController(final ClientRepository clientRepository, final UserService userService,
                            final ClientDTOResourceAssembler clientDTOResourceAssembler) {
        this.clientRepository = clientRepository;
        this.userService = userService;
        this.clientDTOResourceAssembler = clientDTOResourceAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<ClientDTO>> allClients() {
        LOG.debug("finding allClients");
        UserEntity owner = userService.getAuthenticatedUser();
        final List<ClientDTO> clients =
                clientRepository.findAllByOwner(owner).stream().map(ClientDTO::fromClient).collect(Collectors.toList());
        List<EntityModel<ClientDTO>> cr =
                clients.stream().map(clientDTOResourceAssembler::toModel).collect(Collectors.toList());

        return new CollectionModel<>(cr, linkTo(methodOn(ClientController.class).allClients()).withSelfRel());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
    public EntityModel<ClientDTO> newClient(@RequestBody @Valid final ClientDTO client) {
        LOG.debug("creating newClient");
        // TODO extract to service
        UserEntity owner = userService.getAuthenticatedUser();
        client.setOwner(owner.getId());
        ClientDTO clientDTO = ClientDTO.fromClient(clientRepository.save(client.toClient(owner)));
        return clientDTOResourceAssembler.toModel(clientDTO);
    }

    @GetMapping(path = "/{id}")
    public EntityModel<ClientDTO> oneClient(@PathVariable(value = "id") final UUID id) {
        LOG.debug("searching oneClient {}", id);
        UserEntity owner = userService.getAuthenticatedUser();
        Client client = clientRepository.findByIdAndOwner(id, owner).orElseThrow(() -> new ClientNotFoundException(id));
        return clientDTOResourceAssembler.toModel(ClientDTO.fromClient(client));
    }

    @PutMapping(path = "/{id}")
    public EntityModel<ClientDTO> replaceClient(@RequestBody final ClientDTO newClient, @PathVariable final UUID id) {
        LOG.info("replaceClient");
        // TODO verify authenticated permission
        //TODO verify DTO integrity
        Client savedClient = clientRepository.findById(id).map(client -> {
            client.setName(newClient.getName());
            return clientRepository.save(client);
        }).orElseGet(() -> {
            newClient.setId(id);
            // TODO extract to service
            UserEntity owner = userService.getAuthenticatedUser();
            return clientRepository.save(newClient.toClient(owner));
        });

        return clientDTOResourceAssembler.toModel(ClientDTO.fromClient(savedClient));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{id}")
    public void deleteClient(@PathVariable final UUID id) {
        LOG.debug("trying to deleteClient {}", id);
        //TODO verify authenticated permission
        clientRepository.deleteById(id);
    }

}
