package de.diddiz.codegeneration.compiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

/**
 * Based on http://javapracs.blogspot.de/2011/06/dynamic-in-memory-compilation-using.html by Rekha Kumari
 * (June 2011)
 */
public final class InMemoryCompiler
{
	private final JavaCompiler compiler;
	private final JavaFileManager fileManager;

	public InMemoryCompiler() {
		compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null)
			throw new RuntimeException("ToolProvider.getSystemJavaCompiler() returned null! This program needs to be run on a system with an installed JDK.");

		fileManager = new ForwardingJavaFileManager<JavaFileManager>(compiler.getStandardFileManager(null, null, null)) {
			private final Map<String, ByteArrayOutputStream> byteStreams = new HashMap<>();

			@Override
			public ClassLoader getClassLoader(final Location location) {
				return new SecureClassLoader() {
					@Override
					protected Class<?> findClass(final String className) throws ClassNotFoundException {
						final ByteArrayOutputStream bos = byteStreams.get(className);
						if (bos == null)
							return null;
						final byte[] b = bos.toByteArray();
						return super.defineClass(className, b, 0, b.length);
					}
				};
			}

			@Override
			public JavaFileObject getJavaFileForOutput(final Location location, final String className, final JavaFileObject.Kind kind, final FileObject sibling) throws IOException {
				return new SimpleJavaFileObject(URI.create("string:///" + className.replace('.', '/') + kind.extension), kind) {
					@Override
					public OutputStream openOutputStream() throws IOException {
						ByteArrayOutputStream bos = byteStreams.get(className);
						if (bos == null) {
							bos = new ByteArrayOutputStream();
							byteStreams.put(className, bos);
						}
						return bos;
					}
				};
			}
		};
	}

	public boolean compile(final List<? extends SimpleJavaFileObject> classSourceCodes) {
		if (classSourceCodes.isEmpty())
			throw new IllegalArgumentException("Source files lit is empty");

		if (classSourceCodes.size() > 0) {
			final JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, new DiagnosticCollector<>(), null, null, classSourceCodes);
			final Boolean success = task.call();
			return success != null && success;
		}
		return false;
	}

	public Class<?> getCompiledClass(final String className) throws ClassNotFoundException {
		final Class<?> ret = fileManager.getClassLoader(null).loadClass(className);
		if (ret == null)
			throw new ClassNotFoundException("Class returned by ClassLoader was null!");
		return ret;
	}

	public void runMain(final String className, final String[] args) throws IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
		final Class<?> theClass = getCompiledClass(className);
		final Method mainMethod = theClass.getDeclaredMethod("main", String[].class);
		mainMethod.invoke(null, new Object[]{args});
	}

	final public static class IMCSourceCode extends SimpleJavaFileObject
	{
		final public String fullClassName;
		final public String sourceCode;

		/**
		 * @param fullClassName Full name of the class that will be compiled. If the class should be in some package,
		 *        fullName should contain it too, for example: "testpackage.DynaClass"
		 * @param sourceCode the source code
		 */
		public IMCSourceCode(final String fullClassName, final String sourceCode) {
			super(URI.create("string:///" + fullClassName.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
			this.fullClassName = fullClassName;
			this.sourceCode = sourceCode;
		}

		@Override
		public CharSequence getCharContent(final boolean ignoreEncodingErrors) {
			return sourceCode;
		}
	}
}