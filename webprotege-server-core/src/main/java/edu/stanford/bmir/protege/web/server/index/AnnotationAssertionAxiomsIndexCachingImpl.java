package edu.stanford.bmir.protege.web.server.index;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.server.inject.project.RootOntology;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyImpl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 17 Jun 2018
 */
@ProjectSingleton
public class AnnotationAssertionAxiomsIndexCachingImpl implements AnnotationAssertionAxiomsIndex {

    private final OWLOntology rootOntology;

    private final Cache<IRI, ImmutableCollection<OWLAnnotationAssertionAxiom>> axiomsBySubject;
    
    @Inject
    public AnnotationAssertionAxiomsIndexCachingImpl(@RootOntology OWLOntology rootOntology) {
        this.rootOntology = checkNotNull(rootOntology);
        axiomsBySubject = CacheBuilder.newBuilder()
                                      .build();
    }

    @Nonnull
    public Runnable attachOntologyListener() {
        OWLOntologyChangeListener listener = this::handleOntologyChanged;
        rootOntology.getOWLOntologyManager().addOntologyChangeListener(listener);
        return () -> rootOntology.getOWLOntologyManager().removeOntologyChangeListener(listener);
    }

    private void handleOntologyChanged(List<? extends OWLOntologyChange> changes) {
        changes.stream()
               .filter(OWLOntologyChange::isAxiomChange)
               .map(OWLOntologyChange::getAxiom)
               .filter(ax -> ax instanceof OWLAnnotationAssertionAxiom)
               .map(ax -> (OWLAnnotationAssertionAxiom) ax)
               .map(OWLAnnotationAssertionAxiom::getSubject)
               .forEach(axiomsBySubject::invalidate);

    }

    @Override
    public Stream<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(@Nonnull IRI subject) {
        return getAxioms(subject).stream();
    }

    private ImmutableCollection<OWLAnnotationAssertionAxiom> getAxioms(@Nonnull IRI subject) {
        try {
            return axiomsBySubject.get(subject, () -> ImmutableList.copyOf(rootOntology.getAnnotationAssertionAxioms(subject)));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stream<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(@Nonnull IRI subject, @Nonnull OWLAnnotationProperty property) {
        return getAnnotationAssertionAxioms(subject).filter(ax -> ax.getProperty().equals(property));
    }

    @Override
    public long getAnnotationAssertionAxiomsCount(@Nonnull IRI subject) {
        return getAxioms(subject).size();
    }

    @Override
    public long getAnnotationAssertionAxiomsCount(@Nonnull IRI subject, @Nonnull OWLAnnotationProperty property) {
        return getAnnotationAssertionAxioms(subject, property).count();
    }
}
