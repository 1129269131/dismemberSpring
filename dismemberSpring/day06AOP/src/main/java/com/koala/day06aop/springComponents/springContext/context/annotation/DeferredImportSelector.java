package com.koala.day06aop.springComponents.springContext.context.annotation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

/**
 * Create by koala on 2021-09-05
 */
public interface DeferredImportSelector extends ImportSelector {

    /**
     * Return a specific import group.
     * <p>The default implementations return {@code null} for no grouping required.
     * @return the import group class, or {@code null} if none
     * @since 5.0
     */
    @Nullable
    default Class<? extends Group> getImportGroup() {
        return null;
    }


    /**
     * Interface used to group results from different import selectors.
     * @since 5.0
     */
    interface Group {

        /**
         * Process the {@link AnnotationMetadata} of the importing @{@link Configuration}
         * class using the specified {@link DeferredImportSelector}.
         */
        void process(AnnotationMetadata metadata, DeferredImportSelector selector);

        /**
         * Return the {@link Entry entries} of which class(es) should be imported
         * for this group.
         */
        Iterable<Entry> selectImports();


        /**
         * An entry that holds the {@link AnnotationMetadata} of the importing
         * {@link Configuration} class and the class name to import.
         */
        class Entry {

            private final AnnotationMetadata metadata;

            private final String importClassName;

            public Entry(AnnotationMetadata metadata, String importClassName) {
                this.metadata = metadata;
                this.importClassName = importClassName;
            }

            /**
             * Return the {@link AnnotationMetadata} of the importing
             * {@link Configuration} class.
             */
            public AnnotationMetadata getMetadata() {
                return this.metadata;
            }

            /**
             * Return the fully qualified name of the class to import.
             */
            public String getImportClassName() {
                return this.importClassName;
            }

            @Override
            public boolean equals(@Nullable Object other) {
                if (this == other) {
                    return true;
                }
                if (other == null || getClass() != other.getClass()) {
                    return false;
                }
                Entry entry = (Entry) other;
                return (this.metadata.equals(entry.metadata) && this.importClassName.equals(entry.importClassName));
            }

            @Override
            public int hashCode() {
                return (this.metadata.hashCode() * 31 + this.importClassName.hashCode());
            }

            @Override
            public String toString() {
                return this.importClassName;
            }
        }
    }

}
