package org.cyclopsgroup.kaufman.tests;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

public class PackageScanner {
	private PackageScanner() {
	}

	public static interface ClassVisitor {
		void visitClass(Class<?> type);
	}

	public static void scanPackage(String packageName, ClassVisitor visitor)
			throws IOException {
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(
				resolver);
		Resource[] resources = resolver
				.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
						+ ClassUtils
								.convertClassNameToResourcePath(packageName)
						+ "/*.class");
		for (Resource resource : resources) {
			MetadataReader reader = readerFactory.getMetadataReader(resource);
			String className = reader.getClassMetadata().getClassName();

			try {
				Class<?> type = Class.forName(className);
				visitor.visitClass(type);
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("Can't visit class "
						+ className, e);
			}
		}
	}
}
