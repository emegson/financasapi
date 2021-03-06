package br.com.controle.financeiro.controller.api.linkbuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.controle.financeiro.model.dto.TransactionDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TransactionDTOResourceAssembler.class })
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class TransactionDTOResourceAssemblerTest {

	@InjectMocks
	private TransactionDTOResourceAssembler resourceAssembler;

	@Test
	public void testToResource() {
		TransactionDTO transactionMock = new TransactionDTO();
		transactionMock.setId(UUID.randomUUID());
		EntityModel<TransactionDTO> response = resourceAssembler.toModel(transactionMock);
		assertTrue(response.hasLinks());
		assertNotNull(response.getLink("self"));
		assertNotNull(response.getLink("transactions"));
		assertEquals(transactionMock, response.getContent());
	}

}
