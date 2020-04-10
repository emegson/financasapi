package br.com.controle.financeiro.controller.linkbuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.controle.financeiro.model.dto.ClientDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ClientDTOResourceAssembler.class })
@AutoConfigureMockMvc
public class ClientDTOResourceAssemblerTest {

	@InjectMocks
	private ClientDTOResourceAssembler resourceAssembler;


	@Test
	public void testToResource() {
		ClientDTO clientMock = new ClientDTO();
		clientMock.setId(1L);
		clientMock.setName("mock");
		Resource<ClientDTO> response = resourceAssembler.toResource(clientMock);
		assertTrue(response.hasLinks());
		assertNotNull(response.getLink("self"));
		assertNotNull(response.getLink("clients"));
		assertEquals(clientMock, response.getContent());
	}

}