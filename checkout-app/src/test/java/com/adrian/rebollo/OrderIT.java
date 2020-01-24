package com.adrian.rebollo;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

import com.adrian.rebollo.dto.order.NewOrderDto;
import com.adrian.rebollo.dto.order.OrderDto;
import com.adrian.rebollo.dto.product.ProductDto;
import com.adrian.rebollo.error.validation.FieldError;
import com.adrian.rebollo.error.validation.ValidationError;
import com.adrian.rebollo.util.ErrorMessages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
@ExtendWith(PostgresContainerExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = TestConfiguration.class, initializers = { PostgresContainerExtension.Initializer.class })
class OrderIT {

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
    void createOrderTest() throws Exception {

        final ProductDto introducedProduct = new ProductDto("firstProduct", 50, "EUR");
        String productResponse = mockMvc.perform(MockMvcRequestBuilders.post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(introducedProduct)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        Assert.assertNotNull(productResponse);

        String productId = productResponse.substring(productResponse.lastIndexOf("/") + 1);

        final List<UUID> productIds = Collections.singletonList(UUID.fromString(productId));
        final NewOrderDto newOrder = new NewOrderDto("EUR", "email@email.com", productIds);

        final String orderResponse = mockMvc.perform(MockMvcRequestBuilders.post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(newOrder)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        final OrderDto order = objectMapper.readValue(orderResponse, OrderDto.class);

        Assert.assertEquals(50.0, order.getPrice().doubleValue(), 1.0);
        Assert.assertEquals(newOrder.getEmail(), order.getEmail());
        Assert.assertEquals(newOrder.getCurrency(), order.getCurrency());

        String ordersByDate = mockMvc.perform(MockMvcRequestBuilders.get("/order/2020-01-01T22:30:22"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assert.assertNotNull(ordersByDate);
    }

    @Test
    @DirtiesContext
    void newOrderValidationByCurrency() throws Exception {

        final NewOrderDto newOrderDto = new NewOrderDto("NaN", "adria@adria.com", Collections.singletonList(UUID.randomUUID()));

        String orderResponse = mockMvc.perform(MockMvcRequestBuilders.post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(newOrderDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Assert.assertNotNull(orderResponse);

        assertValidation(orderResponse, ErrorMessages.CURRENCY_NOT_EXISTS.getMessage());
    }

    @Test
    @DirtiesContext
    void newOrderValidationByMail() throws Exception {

        final NewOrderDto newOrderDto = new NewOrderDto("EUR", "incorrectMail", Collections.singletonList(UUID.randomUUID()));

        String orderResponse = mockMvc.perform(MockMvcRequestBuilders.post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(newOrderDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Assert.assertNotNull(orderResponse);

        assertValidation(orderResponse, ErrorMessages.ORDER_MAIL_EMPTY.getMessage());
    }

    @Test
    @DirtiesContext
    void newOrderValidationByEmptyList() throws Exception {

        final NewOrderDto newOrderDto = new NewOrderDto("EUR", "arm@arm.com", Collections.emptyList());

        String orderResponse = mockMvc.perform(MockMvcRequestBuilders.post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(newOrderDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Assert.assertNotNull(orderResponse);

        assertValidation(orderResponse, ErrorMessages.ORDER_PRODUCTS_EMPTY.getMessage());
    }

    @Test
    @DirtiesContext
    void getOrderValidationByTimePeriod() throws Exception {

        final LocalDateTime timePeriod = LocalDateTime.MAX;
        final String stringDate = timePeriod.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        String orderResponse = mockMvc.perform(MockMvcRequestBuilders.get("/order/" + stringDate))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Assert.assertNotNull(orderResponse);

        assertValidation(orderResponse, ErrorMessages.ORDER_DATE_INVALID.getMessage());
    }

    private void assertValidation(String orderResponse, String msg) throws JsonProcessingException {
        final ValidationError validationError = objectMapper.readValue(orderResponse, ValidationError.class);

        Assert.assertNotNull(validationError);
        Assert.assertEquals(1, validationError.getErrors().size());

        final FieldError fieldError = validationError.getErrors().get(0);

        Assert.assertEquals(400, fieldError.getCode().intValue());
        Assert.assertEquals(msg, fieldError.getMessage());
    }
}
