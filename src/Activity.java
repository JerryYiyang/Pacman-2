public class Activity extends Action{

    private WorldModel world;
    private ImageStore imageStore;
    public Activity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore){
        super(entity);
        this.world = world;
        this.imageStore = imageStore;
    }

    public void executeAction(
            EventScheduler scheduler) {
        MovingObject m = (MovingObject) super.getEntity();
        m.executeActivity(world, imageStore, scheduler);
    }
        public ImageStore getImageStore() {
        return imageStore;
    }

        public WorldModel getWorld() {
        return world;
    }


//        switch (action.entity.getKind()) {
//            case SAPLING:
//                action.entity.executeSaplingActivity(action.entity, action.world,
//                        action.imageStore, scheduler);
//                break;
//
//            case TREE:
//                action.entity.executeTreeActivity(action.entity, action.world,
//                        action.imageStore, scheduler);
//                break;
//
//            case FAIRY:
//                action.entity.executeFairyActivity(action.entity, action.world,
//                        action.imageStore, scheduler);
//                break;
//
//            case DUDE_NOT_FULL:
//                action.entity.executeDudeNotFullActivity(action.entity, action.world,
//                        action.imageStore, scheduler);
//                break;
//
//            case DUDE_FULL:
//                action.entity.executeDudeFullActivity(action.entity, action.world,
//                        action.imageStore, scheduler);
//                break;
//
//            default:
//                throw new UnsupportedOperationException(String.format(
//                        "executeActivityAction not supported for %s",
//                        action.entity.getKind()));
//        }
}

