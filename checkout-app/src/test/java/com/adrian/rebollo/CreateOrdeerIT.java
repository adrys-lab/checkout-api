package com.adrian.rebollo;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
@ExtendWith(PostgresContainerExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = TestConfiguration.class, initializers = { PostgresContainerExtension.Initializer.class })
class CreateOrdeerIT {

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
}
