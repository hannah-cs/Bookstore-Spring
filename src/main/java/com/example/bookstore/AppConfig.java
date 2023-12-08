package com.example.bookstore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public Book book(){
        return new Book();
    }

    @Bean
    public DatabaseManager databaseManager(){
        return new DatabaseManager();
    }

    @Bean
    public BooksDAO booksDAO(){
        return new BooksDAO(databaseManager());
    }

    @Bean
    public BooksController booksController(){
        return new BooksController(booksDAO());
    }
}
