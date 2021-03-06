package com.library.service.service;

import com.library.service.domain.CategoryEntity;
import com.library.service.domain.FeedbackEntity;
import com.library.service.domain.LibraryEntity;
import com.library.service.dto.Book;
import com.library.service.dto.Category;
import com.library.service.dto.Feedback;
import com.library.service.mail.FeedbackSender;
import com.library.service.repository.FeedbackRepository;
import com.library.service.repository.LibraryRepository;
import com.library.service.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.library.service.domain.CategoryEntity.toCategory;
import static com.library.service.domain.FeedbackEntity.toFeedback;
import static com.library.service.domain.LibraryEntity.toBook;

@Service
public class LibraryService {
    @Autowired
    protected LibraryRepository libraryRepository;
    @Autowired
    protected FeedbackRepository feedbackRepository;
    @Autowired
    protected FeedbackSender feedbackSender;
    @Autowired
    protected CategoryRepository categoryRepository;

    public Book createBook(Book book) {
        LibraryEntity libraryEntity = new LibraryEntity();

        libraryEntity.setTitle(book.getTitle());
        libraryEntity.setText(book.getText());
        libraryEntity.setLastModifiedOn(book.getLastModifiedOn());
        libraryEntity.setCategoryId(book.getCategoryId());

        LibraryEntity savedBook = libraryRepository.saveAndFlush(libraryEntity);
        return toBook(savedBook);
    }

    public Category createCategory(Category category) {
        CategoryEntity categoryEntity = new CategoryEntity();

        categoryEntity.setName(category.getName());

        CategoryEntity saveCategory = categoryRepository.saveAndFlush(categoryEntity);
        return toCategory(saveCategory);
    }

    public Feedback sendFeedback(Feedback feedback) {
        FeedbackEntity feedbackEntity = new FeedbackEntity();

        feedbackEntity.setName(feedback.getName());
        feedbackEntity.setEmail(feedback.getEmail());
        feedbackEntity.setFeedback(feedback.getFeedback());


        this.feedbackSender.sendFeedback(
                feedback.getEmail(),
                feedback.getName(),
                feedback.getFeedback());

        FeedbackEntity savedFeedback = feedbackRepository.saveAndFlush(feedbackEntity);
        return toFeedback(savedFeedback);
    }

    public List<Category> getAllCategories() {
        List<CategoryEntity> all = categoryRepository.findAll();
        List<Category> items = new ArrayList<>();

        for (CategoryEntity i : all) {
            items.add(toCategory(i));
        }
        return items;
    }

    public List<Book> getAllBooks() {
        List<LibraryEntity> bookss = libraryRepository.findAll();
        List<Book> items = new ArrayList<>();

        for (LibraryEntity i : bookss) {
            items.add(toBook(i));
        }
        return items;
    }

    public void removeBook(String id) {
        libraryRepository.deleteById(id);
    }

    public void removeCategory(String id) {
        categoryRepository.deleteById(id);
    }

    public Category updateCategory(String id, Category category) {
        CategoryEntity categ = categoryRepository.getOne(id);
        if (categ == null) {
            throw new RuntimeException("Category with id could not be found.");
        }
        categ.setName(category.getName());

        CategoryEntity updatedCateg = categoryRepository.save(categ);
        return toCategory(updatedCateg);
    }

    public Book updateBook(String id, Book book) {
        LibraryEntity bk = libraryRepository.getOne(id);
        if (bk == null) {
            throw new RuntimeException("Category with id could not be found.");
        }
        bk.setTitle(book.getTitle());
        bk.setText(book.getText());

        LibraryEntity updatedBook = libraryRepository.save(bk);
        return toBook(updatedBook);
    }

    public List<Book> getBooksByCategory(String id) {
        List<Book> items = new ArrayList<>();

        List<LibraryEntity> book = libraryRepository.findAllByCategory(id);

        for (LibraryEntity i : book) {
            items.add(toBook(i));
        }
        return items;
    }
}
