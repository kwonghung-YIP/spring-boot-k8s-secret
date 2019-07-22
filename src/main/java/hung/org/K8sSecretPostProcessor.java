package hung.org;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.FileCopyUtils;

public class K8sSecretPostProcessor implements EnvironmentPostProcessor {

	private Logger log;
	
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		
		Map<String,Object> k8sSecrets = new HashMap<String,Object>();

		if (environment.containsProperty("k8s.secret-mount")) {
			String mountPath = environment.getProperty("k8s.secret-mount");
			k8sSecrets.putAll(read(mountPath));		
		} else {
			int i = 0;
			while (environment.containsProperty("k8s.secret-mount["+i+"]")) {
				String mountPath = environment.getProperty("k8s.secret-mount["+i+"]");
				k8sSecrets.putAll(read(mountPath));
				i++;
			}
		}

		if (!k8sSecrets.isEmpty()) {
			k8sSecrets
			  .entrySet()
			    .forEach(entry -> {
			    	System.out.println(entry.getKey()+"=\""+entry.getValue()+"\"");
			    });
			
			MapPropertySource pptySource = new MapPropertySource("k8s-secrets",k8sSecrets);
			
			environment.getPropertySources().addLast(pptySource);
		}
	}
	
	private Map<String,Object> read(String mountPathPpty) {
		System.out.println(mountPathPpty);
				
		File mountFolder = new File(mountPathPpty);
		if (!mountFolder.isDirectory()) {
			return null;			
		} else {
			Map<String,Object> result = 
			  Stream.of(mountFolder.listFiles())
				.filter(file -> file.isFile())
				  .collect(
			        Collectors.toMap(file -> {
			        	return "k8s-secret." + mountFolder.getName() + "." + file.getName();  
			        }, file -> {
			        	try {
			        		byte[] content = FileCopyUtils.copyToByteArray(file);
			        		return new String(content);
			        	} catch (IOException e) {
			        		throw new RuntimeException(e);	
			        	}
			        }
			      ));
			return result;
		}
	}
	

}
