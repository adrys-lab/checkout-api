package com.adrian.rebollo;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.adrian.rebollo.dto.product.ProductDto;
import com.adrian.rebollo.error.validation.FieldError;
import com.adrian.rebollo.error.validation.ValidationError;
import com.adrian.rebollo.util.ErrorMessages;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
@ExtendWith(PostgresContainerExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = TestConfiguration.class, initializers = { PostgresContainerExtension.Initializer.class })
class ProductIT {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    @DirtiesContext
    void createProductTest() throws Exception {

        final ProductDto productDto = new ProductDto("firstProduct", 50, "EUR");

        mockMvc.perform(MockMvcRequestBuilders.post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(productDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DirtiesContext
    void createProductAndRetrieveTest() throws Exception {

        final ProductDto introducedProduct = new ProductDto("second", 150, "USD");

        String postResponse = mockMvc.perform(MockMvcRequestBuilders.post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(introducedProduct)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        Assert.assertNotNull(postResponse);

        String productId = postResponse.substring(postResponse.lastIndexOf("/") + 1);

        String response = mockMvc.perform(MockMvcRequestBuilders.get("/product/" + productId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final ProductDto readProduct = objectMapper.readValue(response, ProductDto.class);

        Assert.assertEquals(readProduct, introducedProduct);
    }

    @Test
    @DirtiesContext
    void newOrderValidationByCurrency() throws Exception {

        final ProductDto productDto = new ProductDto("", 3.0, "EUR");

        String orderResponse = mockMvc.perform(MockMvcRequestBuilders.post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(productDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Assert.assertNotNull(orderResponse);

        assertValidation(orderResponse, ErrorMessages.PRODUCT_NAME_BLANK.getMessage());
    }

    private void assertValidation(String orderResponse, String msg) throws com.fasterxml.jackson.core.JsonProcessingException {
        ValidationError validationError = objectMapper.readValue(orderResponse, ValidationError.class);

        Assert.assertNotNull(validationError);

        Assert.assertEquals(1, validationError.getErrors().size());

        FieldError fieldError = validationError.getErrors().get(0);

        Assert.assertEquals(400, fieldError.getCode().intValue());
        Assert.assertEquals(msg, fieldError.getMessage());
    }
}


