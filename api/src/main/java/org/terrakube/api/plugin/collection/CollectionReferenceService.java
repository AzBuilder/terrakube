package org.terrakube.api.plugin.collection;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.terrakube.api.plugin.collection.model.CollectionReferenceData;
import org.terrakube.api.plugin.collection.model.CollectionReferenceHttpBody;
import org.terrakube.api.repository.CollectionReferenceRepository;
import org.terrakube.api.repository.CollectionRepository;
import org.terrakube.api.repository.WorkspaceRepository;
import org.terrakube.api.rs.collection.Collection;
import org.terrakube.api.rs.collection.CollectionReference;
import org.terrakube.api.rs.workspace.Workspace;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class CollectionReferenceService {

    private final WorkspaceRepository workspaceRepository;
    private final CollectionRepository collectionRepository;
    private final CollectionReferenceRepository collectionReferenceRepository;

    public Optional<CollectionReferenceHttpBody> newCollectionReference(CollectionReferenceHttpBody collectionReferenceHttpBody) {
        Optional<Workspace> workspaceOptional = workspaceRepository.findById(UUID.fromString(collectionReferenceHttpBody.getData().getAttributes().getWorkspaceId()));
        Optional<Collection> collectionReferenceOptional = collectionRepository.findById(UUID.fromString(collectionReferenceHttpBody.getData().getAttributes().getCollectionId()));

        if (workspaceOptional.isPresent() && collectionReferenceOptional.isPresent()) {
            CollectionReference collectionReference = new CollectionReference();
            collectionReference.setWorkspace(workspaceOptional.get());
            collectionReference.setCollection(collectionReferenceOptional.get());
            collectionReference = collectionReferenceRepository.save(collectionReference);
            collectionReferenceHttpBody.getData().setId(collectionReference.getId().toString());
            return Optional.of(collectionReferenceHttpBody);
        } else {
            return Optional.empty();
        }

    }

    public Optional<CollectionReferenceHttpBody> getCollectionReference(String referenceId) {
        Optional<CollectionReference> collectionReference = collectionReferenceRepository.findById(UUID.fromString(referenceId));
        if (collectionReference.isPresent()) {
            CollectionReferenceHttpBody collectionReferenceHttpBody = new CollectionReferenceHttpBody();
            collectionReferenceHttpBody.setData(new CollectionReferenceData());
            collectionReferenceHttpBody.getData().setId(collectionReference.get().getId().toString());
            collectionReferenceHttpBody.getData().setType("collection-reference");
            collectionReferenceHttpBody.getData().getAttributes().setWorkspaceId(collectionReference.get().getWorkspace().getId().toString());
            collectionReferenceHttpBody.getData().getAttributes().setCollectionId(collectionReference.get().getCollection().getId().toString());
            return Optional.of(collectionReferenceHttpBody);
        } else {
            return Optional.empty();
        }
    }

    public Optional<CollectionReferenceHttpBody> updateCollectionReference(CollectionReferenceHttpBody collectionReferenceHttpBody, String referenceId) {
        Optional<CollectionReference> collectionReferenceUpdate = collectionReferenceRepository.findById(UUID.fromString(referenceId));
        if (collectionReferenceUpdate.isPresent()) {
            Optional<Workspace> workspaceOptional = workspaceRepository.findById(UUID.fromString(collectionReferenceHttpBody.getData().getAttributes().getWorkspaceId()));
            Optional<Collection> collectionReferenceOptional = collectionRepository.findById(UUID.fromString(collectionReferenceHttpBody.getData().getAttributes().getCollectionId()));

            if (workspaceOptional.isPresent() && collectionReferenceOptional.isPresent()) {
                CollectionReference collectionReference = collectionReferenceUpdate.get();
                collectionReference.setWorkspace(workspaceOptional.get());
                collectionReference.setCollection(collectionReferenceOptional.get());
                collectionReference = collectionReferenceRepository.save(collectionReference);

                collectionReferenceHttpBody.getData().setId(collectionReference.getId().toString());
                return Optional.of(collectionReferenceHttpBody);
            }
        }
        return Optional.empty();
    }

    public Optional<Boolean> deleteCollectionReference(String referenceId) {
        Optional<CollectionReference> collectionReferenceUpdate = collectionReferenceRepository.findById(UUID.fromString(referenceId));
        if (collectionReferenceUpdate.isPresent()) {
            collectionReferenceRepository.deleteById(UUID.fromString(referenceId));
            return Optional.of(true);
        }
        return Optional.empty();
    }

}
