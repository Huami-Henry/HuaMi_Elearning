package com.huami.elearning.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.huami.elearning.R;
import com.huami.elearning.db.DownloadSqlTool;
import com.huami.elearning.model.FileDescribe;
import com.huami.elearning.model.FileDownInfo;
import com.huami.elearning.ui.NumberProgressBar;
import com.huami.elearning.util.CheckDisk;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.List;
/**
 * Created by Henry on 2017/8/18.
 */

public class DownLoadAdapter extends RecyclerView.Adapter<DownLoadAdapter.DownLoadHolder> {
    private List<FileDownInfo> describes;
    public DownLoadAdapter(List<FileDownInfo> describes) {
        this.describes = describes;
    }

    @Override
    public DownLoadAdapter.DownLoadHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download_manager, parent, false);
        DownLoadHolder holder = new DownLoadHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DownLoadAdapter.DownLoadHolder holder, int position) {
        FileDownInfo describe = describes.get(position);
        holder.name.setText(describe.getFile_name());
        int asset_type = describe.getFile_type();
        double length = 0;
        switch (asset_type) {
            case 1:
                String filepath = CheckDisk.checkState() + "VIDEO/" + describe.getFile_name();
                length = getFileSize(filepath, SIZETYPE_MB);
                break;
            case 2:
                String audioPath = CheckDisk.checkState() + "AUDIO/" + describe.getFile_name();
                length = getFileSize(audioPath, SIZETYPE_MB);
                break;
            case 3:
                String imagePath = CheckDisk.checkState() + "IMAGE/" + describe.getFile_name();
                length = getFileSize(imagePath, SIZETYPE_MB);
                break;
            case 4:
                String textPath = CheckDisk.checkState() + "TEXT/" + describe.getFile_name();
                length = getFileSize(textPath, SIZETYPE_MB);
                break;
            default:
                String otherPath = CheckDisk.checkState() + "OTHER/" + describe.getFile_name();
                length = getFileSize(otherPath, SIZETYPE_MB);
                break;
        }
        double allLength = FormatFileSize(describe.getFile_length(), SIZETYPE_MB);
        holder.downloadSize.setText(length+" MB/"+allLength+" MB");
        holder.pbProgress.setMax(100);
        holder.pbProgress.setProgress((int) ((length / allLength)*100));
    }
    /**
     * 获取文件指定文件的指定单位的大小
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileSize(String filePath,int sizeType){
        File file=new File(filePath);
        long blockSize=0;
        try {
            blockSize = getFileSize(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormatFileSize(blockSize, sizeType);
    }
    private static long getFileSize(File file) throws Exception{
        long size = 0;
        if (file.exists()){
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        }else{
            file.createNewFile();
        }
        return size;
    }
    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值
    /**
     * 转换文件大小,指定转换的类型
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormatFileSize(long fileS,int sizeType){
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong=Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong=Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong=Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong=Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    @Override
    public int getItemCount() {
        return describes.size();
    }
    public class DownLoadHolder extends RecyclerView.ViewHolder {
        private TextView name,downState;
        private TextView downloadSize;
        private NumberProgressBar pbProgress;
        public DownLoadHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            downloadSize = (TextView) itemView.findViewById(R.id.downloadSize);
            pbProgress = (NumberProgressBar) itemView.findViewById(R.id.pbProgress);
        }
    }
}
