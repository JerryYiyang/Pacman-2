public class Animation extends Action{
    private int repeatCount;
    public Animation(
            Entity entity,
            int repeatCount){
        super(entity);
        this.repeatCount = repeatCount;
    }
    public int getRepeatCount() {
        return repeatCount;
    }

    public void executeAction(EventScheduler scheduler)
    {
        super.getEntity().nextImage();
        Animated a = (Animated) super.getEntity();
        if (repeatCount != 1)
        {
            scheduler.scheduleEvent(super.getEntity(),
                    super.getEntity().createAnimationAction(Math.max(repeatCount - 1, 0)),
                    a.getAnimationPeriod());
        }
    }
}
