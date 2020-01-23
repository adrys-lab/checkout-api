package com.adrian.rebollo.controller;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class HomeControllerTest {

    private MockMvc mvc;
    private HomeController swaggerRootController;

    @Test
    public void rootRedirectsToSwaggerDoc() throws Exception {
        swaggerRootController = new HomeController();
        mvc = MockMvcBuilders.standaloneSetup(swaggerRootController).build();
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.redirectedUrl("swagger-ui.html"))
                .andExpect(MockMvcResultMatchers.status().isFound());
    }
}
