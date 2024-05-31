package factory;

import java.util.ArrayList;

public class LevelSelection {

    public ProductType selectedBase;
    public ComponentType[] selectedComponents;

    public LevelSelection(ProductType productType, ArrayList<ComponentType> selectedComponents) {
        this.selectedBase = productType;
        this.selectedComponents = selectedComponents.toArray(new ComponentType[selectedComponents.size()]);
    }

}
