package br.dev.alberto.genia.service;

import br.dev.alberto.genia.model.Pizza;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PizzaService {
    private static final Logger logger = LoggerFactory.getLogger(PizzaService.class);

    private List<Pizza> pizzas;

    @PostConstruct
    public void init() {
        pizzas = new ArrayList<>();
        pizzas.add(new Pizza("Calabresa", "P", 30.0D));
        pizzas.add(new Pizza("Calabresa", "M", 50.0D));
        pizzas.add(new Pizza("Calabresa", "G", 80.0D));
        pizzas.add(new Pizza("Palmito", "P", 33.0D));
        pizzas.add(new Pizza("Palmito", "M", 44.0D));
        pizzas.add(new Pizza("Palmito", "G", 77.0D));
        pizzas.add(new Pizza("Portuguesa", "P", 35.0D));
        pizzas.add(new Pizza("Portuguesa", "M", 55.0D));
        pizzas.add(new Pizza("Portuguesa", "P", 85.0D));
    }

    @Tool(description = "Esse método lista todos os sabores de pizza disponíveis no cardápio")
    public List<String> getPizzas() {
        var ps = this.pizzas.stream().map(Pizza::getSabor)
                .distinct()
                .toList();
        logger.info("Lista de pizzas disponíveis: {}", ps);
        return ps;
    }

    @Tool(description = "Esse método retorna o sabor, tamanho e preço de todas as pizzas disponíveis no cardápio")
    public List<Pizza> getPizzasPreco() {
        logger.info("Lista de pizzas: {}", this.pizzas);
        return this.pizzas;
    }

    @Tool(description = "Dado um sabor, esse método retorna o preço da pizza e seus tamanhos disponíveis")
    public List<Pizza> getPrecoPorSabor(String sabor) {
        logger.info("Sabor escolhido: {}", sabor);
        var result = this.pizzas.stream()
                .filter(pizza -> pizza.getSabor().equalsIgnoreCase(sabor))
                .toList();
        logger.info("Preco do sabor {}: {}", sabor, result);
        return result;
    }

    @Tool(description = "Adiciona um novo sabor de pizza ao cardápio")
    public void novaPizza(String sabor, String tamanho, Double valor) {
        var pizza = new Pizza(sabor, tamanho, valor);
        logger.info("Adicionando novo sabor de pizza: {}", pizza);
        this.pizzas.add(pizza);
    }

}
