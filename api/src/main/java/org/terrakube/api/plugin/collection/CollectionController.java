package org.terrakube.api.plugin.collection;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.terrakube.api.plugin.collection.model.CollectionReferenceHttpBody;

import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/collection")
public class CollectionController {

    private final CollectionReferenceService collectionReferenceService;

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/reference")
    public ResponseEntity<CollectionReferenceHttpBody> newCollectionReference(@RequestBody CollectionReferenceHttpBody collectionReferenceHttpBody) {
           return collectionReferenceService
                   .newCollectionReference(collectionReferenceHttpBody)
                   .map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Transactional
    @PostMapping(produces = "application/vnd.api+json", path = "/reference/{referenceId}")
    public ResponseEntity<CollectionReferenceHttpBody> getCollectionReference(@PathVariable String referenceId) {
        return collectionReferenceService
                .getCollectionReference(referenceId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    @PatchMapping(produces = "application/vnd.api+json", path = "/reference/{referenceId}")
    public ResponseEntity<CollectionReferenceHttpBody> updateCollectionReference(@RequestBody CollectionReferenceHttpBody collectionReferenceHttpBody, @PathVariable String referenceId) {
        return collectionReferenceService
                .updateCollectionReference(collectionReferenceHttpBody, referenceId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Transactional
    @DeleteMapping(produces = "application/vnd.api+json", path = "/reference/{referenceId}")
    public ResponseEntity<Void> deleteCollectionReference(@PathVariable String referenceId) {
        Optional<Boolean> deleted = collectionReferenceService.deleteCollectionReference(referenceId);

        if (deleted.isPresent() && deleted.get()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }

    }
}
