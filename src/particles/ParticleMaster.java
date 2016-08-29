package particles;

import entities.Camera;
import org.joml.Matrix4f;
import loaders.Loader;

import java.util.*;

/**
 * Created by one on 7/31/16.
 */
public class ParticleMaster {

    private static Map<ParticleTexture, List<Particle>> particles = new HashMap<ParticleTexture, List<Particle>>();
    private static ParticleRenderer particleRenderer;

    public static void init(Loader loader, Matrix4f projectionMatrix) {

        particleRenderer = new ParticleRenderer(loader, projectionMatrix);

    }

    public static void update(Camera camera) {

        Iterator<Map.Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
        while (mapIterator.hasNext()) {
            List<Particle> list = mapIterator.next().getValue();
            Iterator<Particle> iterator = list.iterator();
            while (iterator.hasNext()) {
                Particle p = iterator.next();
                boolean stillAlive = p.update(camera);
                if (!stillAlive) {
                    iterator.remove();
                    if (list.isEmpty()) {
                        mapIterator.remove();
                    }
                }
            }

            InsertionSort.sortHighToLow(list);

        }

    }

    public static void renderParticles(Camera camera) {
        particleRenderer.render(particles, camera);
    }

    public static void cleanUp() {
        particleRenderer.cleanUp();
    }

    public static void addParticle(Particle particle) {
        List<Particle> list = particles.get(particle.getTexture());
        if (list == null) {
            list = new ArrayList<Particle>();
            particles.put(particle.getTexture(), list);
        }
        list.add(particle);
    }
}
