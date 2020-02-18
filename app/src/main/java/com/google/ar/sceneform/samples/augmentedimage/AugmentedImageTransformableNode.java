package com.google.ar.sceneform.samples.augmentedimage;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.concurrent.CompletableFuture;


public class AugmentedImageTransformableNode  extends AnchorNode {

    private static final String TAG = "AugmentedImageNode";

    // The augmented image represented by this node.
    private AugmentedImage image;
    //private static ModelRenderable main;
    private static CompletableFuture<ModelRenderable> main;


    public AugmentedImageTransformableNode (Context context, String imageName) {

        main = ModelRenderable.builder()
                .setSource(context, Uri.parse(getMainPath(imageName)))
                .build();
        }


    /**
     * Called when the AugmentedImage is detected and should be rendered. A Sceneform node tree is
     * created based on an Anchor created from the image. The corners are then positioned based on the
     * extents of the image. There is no need to worry about world coordinates since everything is
     * relative to the center of the image, which is the parent node of the corners.
     */
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    public void setImage(AugmentedImage image, ArFragment arFragment) {
        this.image = image;

        if(!main.isDone()){
            CompletableFuture.allOf(main)
                    .thenAccept((Void aVoid) -> setImage(image, arFragment))
                    .exceptionally(
                            throwable -> {
                                Log.e(TAG, "Exception loading", throwable);
                                return null;
                            });
        }

        setAnchor(image.createAnchor(image.getCenterPose()));


        Vector3 localPosition = new Vector3();
        localPosition.set(-0.25f *image.getExtentX(), 0.0f,-0.25f *image.getExtentZ());

        TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
        andy.setLocalPosition(localPosition);
        andy.setRenderable(main.getNow(null));
        andy.setLookDirection(Vector3.left());

        andy.setParent(this);

    }

    public AugmentedImage getImage() {
        return image;
    }


    public ModelRenderable getRendable(){
        return main.getNow(null);
    }

    private String getMainPath(String name){
        String toReturn ="";

        if(name.equals("gioconda")){
            toReturn="models/andy.sfb";

        }else if(name.equals("urlo")){
            toReturn="models/urlo.sfb";

        }else{
            toReturn="models/andy.sfb";

        }

        return toReturn;
    }
}
