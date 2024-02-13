package com.synapps.atch.test.controller;


import com.synapps.atch.test.entity.Test;
import com.synapps.atch.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tests")
public class TestController {

    @Autowired
    private TestService testService;

    @PostMapping
    public ResponseEntity<Test> createTest(@RequestBody Test test) {
        testService.createTest(test);
        return new ResponseEntity<>(test, HttpStatus.CREATED);
    }

    @GetMapping("/{testId}")
    public ResponseEntity<Test> getTest(@PathVariable String testId) {
        Test test = testService.getTest(testId);
        if (test != null) {
            return new ResponseEntity<>(test, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{testId}")
    public ResponseEntity<String> updateTest(@PathVariable String testId, @RequestBody Test test) {
        Test existingProduct = testService.getTest(testId);
        if (existingProduct != null) {
            testService.updateTest(test);
            return new ResponseEntity<>("Test updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Test not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{testId}")
    public ResponseEntity<String> deleteTest(@PathVariable String testId) {
        testService.deleteProduct(testId);
        return new ResponseEntity<>("Test deleted successfully", HttpStatus.OK);
    }
}
