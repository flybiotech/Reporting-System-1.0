#include "ThDecode.h"
#include "four.h"
int TEXTURE_WID = 512; //the texture width and height
int TEXTURE_HEI = 256;
int d_w = 480; //the device width and height
int d_h = 360;
int expand_w;
int expand_h;
int img_size;
static GLuint texture[4] = 
{
  0
};
extern TdecParam decParam[4];
extern int decHandle[4];
extern TDevInfoCfg Cfg[4];
int thDecodeVideoInit(TdecParam* Param)
{
  pthread_mutex_lock(&lock);
  TdecPkt* pdec = NULL;
  pdec = (TdecPkt*)malloc(sizeof(TdecPkt));
  memset(pdec, 0, sizeof(TdecPkt));
  av_register_all();
  avcodec_register_all();
  LOGE("===============0");
  pdec->FrameV = avcodec_alloc_frame();
  if (pdec->FrameV == NULL)
    goto exits;

  pdec->CodecContextV = avcodec_alloc_context();
  if (pdec->CodecContextV == NULL)
    goto exits;

  pdec->CodecContextV->codec_type = AVMEDIA_TYPE_VIDEO;
  //pdec->CodecContextV->codec_id = CODEC_ID_H264;

  pdec->CodecContextV->time_base.num = 1; //这两行：一秒钟25�?    pdec->CodecContextV->time_base.den = 50;
  pdec->CodecContextV->bit_rate = 0; //初始化为0
  pdec->CodecContextV->frame_number = 1; //每包一个视频帧
  if (Param->play_type == 0)
  {
    pdec->CodecContextV->width = Param->decWidth; //这两行：视频的宽度和高度
    pdec->CodecContextV->height = Param->decHeight;
  }
  else if (Param->play_type == 1)
  {
    pdec->CodecContextV->width = Param->decWidthMain; //这两行：视频的宽度和高度
    pdec->CodecContextV->height = Param->decHeightMain;
  }

  pdec->CodecContextV->pix_fmt = PIX_FMT_YUV420P;


  pdec->CodecV = avcodec_find_decoder(CODEC_ID_H264);
  if (pdec->CodecV == NULL)
    goto exits;

  if (avcodec_open2(pdec->CodecContextV, pdec->CodecV, NULL) < 0)
    goto exits;
  LOGE("===============1");

  pdec->img_convert_ctx = sws_getContext(pdec->CodecContextV->width, pdec->CodecContextV->height, pdec->CodecContextV->pix_fmt, TEXTURE_WID, TEXTURE_HEI, PIX_FMT_RGB565, SWS_POINT, NULL, NULL, NULL);
  if (pdec->img_convert_ctx == NULL)
  {
    LOGE("could not initialize conversion contex,w is \n");
    goto exits;
  }

  img_size = sizeof(uint16_t)* TEXTURE_WID * TEXTURE_HEI;
  pdec->pFrameRGB = avcodec_alloc_frame();
  if (pdec->pFrameRGB == NULL)
  {
    goto exits;
  }
  pdec->s_pixels = (uint8_t*)malloc(img_size);
  pdec->b_pixels = (uint8_t*)malloc(img_size);
  memset(pdec->s_pixels, 0x00, img_size);
  memset(pdec->b_pixels, 0x00, img_size);
  avpicture_fill((AVPicture*)pdec->pFrameRGB, (uint8_t*)pdec->s_pixels, PIX_FMT_RGB565, TEXTURE_WID, TEXTURE_HEI);
  pthread_mutex_unlock(&lock);
  return (int)pdec;

exits: LOGE("===============2");
  pthread_mutex_unlock(&lock);
  thDecodeVideoFree((int*)pdec);
  //[self thDecodeVideoFree:&pdec];
  return 0;
}

int thDecodeVideoFrame(int decHandle, TdecParam* Param, int channel)
{
  char* Buf;
  int Len;
  int Size, GotPicture;

  TdecPkt* pdec = (TdecPkt*)decHandle;
  if (!pdec)
    return false;
  if (!pdec->CodecContextV)
    return false;
  if (!pdec->CodecV)
    return false;
  if (!pdec->FrameV)
    return false;
  if (Param->encBuf == NULL && Param->encLen > 0)
    return false;

  Buf = Param->encBuf;
  Len = Param->encLen;

  AVPacket packet;
  av_init_packet(&packet);
  packet.data = (uint8_t*)Buf;
  packet.size = Len;

  while (packet.size > 0)
  {
    Size = avcodec_decode_video2(pdec->CodecContextV, pdec->FrameV, &GotPicture, &packet);

    if (GotPicture == 0)
    {
      avcodec_decode_video2(pdec->CodecContextV, pdec->FrameV, &GotPicture, &packet);
      break;
    }
    packet.size -= Size;
    packet.data += Size;
  }

  // Size = avcodec_decode_video2(pdec->CodecContextV, pdec->FrameV, &GotPicture, &packet);
  // Param->decLen =Param->decLen+ Size;
  //LOGE("decode byte is %d,channel is %d",Size,channel);
  if (Size > 0)
  {
    if (_expand_channel == channel)
    {
      if (_capture)
      {

        _capture = false;

        unsigned char* rgbBuf = (unsigned char*)malloc(pdec->CodecContextV->width* pdec->CodecContextV->height* 3);

        struct SwsContext* img_convert_ctx_pic = 0;
        int linesize[4] = 
        {
          3* pdec->CodecContextV->width, 0, 0, 0
        };

        img_convert_ctx_pic = sws_getContext(pdec->CodecContextV->width, pdec->CodecContextV->height, PIX_FMT_YUV420P, pdec->CodecContextV->width, pdec->CodecContextV->height, PIX_FMT_BGR24, SWS_FAST_BILINEAR, 0, 0, 0);

        if (img_convert_ctx_pic != 0)
        {
          sws_scale(img_convert_ctx_pic, pdec->FrameV->data, pdec->FrameV->linesize, 0, pdec->CodecContextV->height, (uint8_t**) &rgbBuf, linesize);
          sws_freeContext(img_convert_ctx_pic);
          saveFrame(rgbBuf, _picname, pdec->CodecContextV->width, pdec->CodecContextV->height); //生成图片  
        }
        free(rgbBuf);
      }
      if (_record)
      {
        LOGE("channel is %d,expand channel is %d", channel, _expand_channel);
        if (_recordstate)
        {
          LOGE("--------------------------0");
          _recordstate = false;
          fmt = av_guess_format(NULL, _filename, NULL);
          if (!fmt)
          {
            printf("Could not deduce output format from file extension: using MPEG.\n");
            fmt = av_guess_format("mpeg", NULL, NULL);
          }
          if (!fmt)
          {
            fprintf(stderr, "Could not find suitable output format\n");
            exit(1);
          }
          LOGE("--------------------------1");
          /* allocate the output media context */
          oc = avformat_alloc_context();
          if (!oc)
          {
            fprintf(stderr, "Memory error\n");
            exit(1);
          }
          oc->oformat = fmt;
          snprintf(oc->filename, sizeof(oc->filename), "%s", _filename);
          LOGE("--------------------------2");
          oc->oformat->video_codec = CODEC_ID_MPEG4;

          fmt = oc->oformat;
          video_st = NULL;
          LOGE("--------------------------3");
          if (fmt->video_codec != CODEC_ID_NONE)
          {
            video_st = add_video_stream(oc, fmt->video_codec, pdec->CodecContextV->width, pdec->CodecContextV->height);
          }

          LOGE("--------------------------4");
          av_dump_format(oc, 0, _filename, 1);

          if (video_st)
          {
            open_video(oc, video_st);
          }

          /* open the output file, if needed */
          if (!(fmt->flags &AVFMT_NOFILE))
          {
            if (avio_open(&oc->pb, _filename, AVIO_FLAG_WRITE) < 0)
            {
              fprintf(stderr, "Could not open '%s'\n", _filename);
              return  - 1;
            }
          }

          /* write the stream header, if any */
          avformat_write_header(oc, NULL);
          picture->pts = 0;

        }
        int out_size, ret;
        AVCodecContext* c;
        //static struct SwsContext *img_convert_ctx;
        c = video_st->codec;
        out_size = avcodec_encode_video(c, video_outbuf, video_outbuf_size, pdec->FrameV);

        if (out_size > 0)
        {
          AVPacket pkt;
          av_init_packet(&pkt);

          //  if (c->coded_frame->pts != AV_NOPTS_VALUE)
          //     pkt.pts= av_rescale_q(c->coded_frame->pts, c->time_base, video_st->time_base);
          // if(c->coded_frame->key_frame)
          //     pkt.flags |= AV_PKT_FLAG_KEY;
          pkt.stream_index = video_st->index;
          pkt.data = video_outbuf;
          pkt.size = out_size;
          LOGE("--------------------------5");

          ret = av_interleaved_write_frame(oc, &pkt);
        }
        else
        {
          ret = 0;
        }
      }
      if (_recordchanged)
      {
        _recordchanged = false;
        av_write_trailer(oc);
        if (video_st)
          //close_video(oc, video_st);
        {
          int i = 0;
          for (i = 0; i < oc->nb_streams; i++)
          {
            av_freep(&oc->streams[i]->codec);
            av_freep(&oc->streams[i]);
          }
        }

        if (!(fmt->flags &AVFMT_NOFILE))
        {
          avio_close(oc->pb);
        }
        av_free(oc);
      }
    }


    sws_scale(pdec->img_convert_ctx, (const uint8_t* const*)pdec->FrameV->data, pdec->FrameV->linesize, 0, pdec->CodecContextV->height, pdec->pFrameRGB->data, pdec->pFrameRGB->linesize);
  }
  //#warning " 6666666666666 "
  return 0;
}

int thDecodeVideoFree(int* decHandle)
{
  TdecPkt* pdec = (TdecPkt*) * decHandle;
  if (!pdec)
    return false;

  //1
  if (pdec->CodecContextV)
  {
    avcodec_close(pdec->CodecContextV);
    pdec->CodecContextV = NULL;
  }
  //2
  pdec->CodecV = NULL;
  //3
  if (pdec->FrameV)
  {
    av_free(pdec->FrameV);
    pdec->FrameV = NULL;
  }
  //4
  //av_free_static();

  memset(pdec, 0, sizeof(TdecPkt));
  free(pdec);
  *decHandle = 0;
  return 0;
}

static GLuint s_disable_caps[] = 
{
  GL_FOG, GL_LIGHTING, GL_CULL_FACE, GL_ALPHA_TEST, GL_BLEND, GL_COLOR_LOGIC_OP, GL_DITHER, GL_STENCIL_TEST, GL_DEPTH_TEST, GL_COLOR_MATERIAL, 0
}; /*open gl*/
static void check_gl_error(const char* op)
{
  GLint error;
  for (error = glGetError(); error; error = glGetError())
    LOGI("after %s() glError (0x%x)\n", op, error);
}

void native_gl_resize_channel(JNIEnv* env, jclass clazz, jint w, jint h, jint channel, jint expand)
{
  //   LOGE("===================0 native_gl_resize %d %d,channel:%d", w, h,channel);
  if ( - 1 == channel)
  {
    return ;
  }
  if (expand == 1)
  {
    expand_w = w; //the device width and height
    expand_h = h;
  }
  else
  {
    d_w = w; //the device width and height
    d_h = h;
  }

  glDeleteTextures(1, &texture[channel]);
  GLuint* start = s_disable_caps;
  while (*start)
    glDisable(*start++);
  glEnable(GL_TEXTURE_2D);
  glGenTextures(1, &texture[channel]);
  glBindTexture(GL_TEXTURE_2D, texture[channel]);
  glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
  glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
  glShadeModel(GL_FLAT);
  check_gl_error("glShadeModel");
  glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
  check_gl_error("glColor4x");
  int rect[4] = 
  {
    0, TEXTURE_HEI, TEXTURE_WID,  - TEXTURE_HEI
  };
  glTexParameteriv(GL_TEXTURE_2D, GL_TEXTURE_CROP_RECT_OES, rect);
  check_gl_error("glTexParameteriv");
  //LOGE("===================1 native_gl_resize %d %d,channel:%d", w, h,channel);
}

int native_gl_render_channel(JNIEnv* env, jclass clazz, jint channel, jint expand)
{
  if ( - 1 == channel)
  {
    if (_expand)
    {
      int size = sizeof(uint16_t)* TEXTURE_WID * TEXTURE_HEI;
      uint8_t* b_pixels = (uint8_t*)malloc(size);
      memset(b_pixels, 0x00, size);
      glClear(GL_COLOR_BUFFER_BIT);
      glTexImage2D(GL_TEXTURE_2D,  /* target */
        0,  /* level */
        GL_RGB,  /* internal format */
        TEXTURE_WID,  /* width */
        TEXTURE_HEI,  /* height */
        0,  /* border */
        GL_RGB,  /* format */
        GL_UNSIGNED_SHORT_5_6_5,  /* type */
        b_pixels); /* pixels */
      check_gl_error("glTexImage2D");
      glDrawTexiOES(0, 0, 0, expand_w, expand_h);
      free(b_pixels);
    }
    _expand = false;
    _expand_channel =  - 1;
    return 0;
  }
  if (decParam[channel].init == 0)
  {

    if (1 == expand)
    {
      int size = sizeof(uint16_t)* TEXTURE_WID * TEXTURE_HEI;
      uint8_t* b_pixels = (uint8_t*)malloc(size);
      memset(b_pixels, 0x00, size);
      glClear(GL_COLOR_BUFFER_BIT);
      glTexImage2D(GL_TEXTURE_2D,  /* target */
        0,  /* level */
        GL_RGB,  /* internal format */
        TEXTURE_WID,  /* width */
        TEXTURE_HEI,  /* height */
        0,  /* border */
        GL_RGB,  /* format */
        GL_UNSIGNED_SHORT_5_6_5,  /* type */
        b_pixels); /* pixels */
      check_gl_error("glTexImage2D");
      glDrawTexiOES(0, 0, 0, expand_w, expand_h);
      free(b_pixels);
    }
    return 0;
  }
  //LOGE("=================== 0 native_gl_render,channel:%d", channel);
  TdecPkt* pdec = (TdecPkt*)decHandle[channel];
  //LOGE("=================== 1 native_gl_render,channel:%d", channel);
  glClear(GL_COLOR_BUFFER_BIT);
  if (!thNet_IsConnect(Cfg[channel].NetHandle))
  {
    glTexImage2D(GL_TEXTURE_2D,  /* target */
      0,  /* level */
      GL_RGB,  /* internal format */
      TEXTURE_WID,  /* width */
      TEXTURE_HEI,  /* height */
      0,  /* border */
      GL_RGB,  /* format */
      GL_UNSIGNED_SHORT_5_6_5,  /* type */
      pdec->b_pixels); /* pixels */
  }
  else
  {
    glTexImage2D(GL_TEXTURE_2D,  /* target */
      0,  /* level */
      GL_RGB,  /* internal format */
      TEXTURE_WID,  /* width */
      TEXTURE_HEI,  /* height */
      0,  /* border */
      GL_RGB,  /* format */
      GL_UNSIGNED_SHORT_5_6_5,  /* type */
      pdec->s_pixels); /* pixels */
  }

  check_gl_error("glTexImage2D");
  if (1 == expand)
  {
    _expand = true;
    _expand_channel = channel;
    glDrawTexiOES(0, 0, 0, expand_w, expand_h);
    //    LOGE("=================== 2 native_gl_render,w is %d,hid is %d,channel:%d", channel,expand_w,expand_h);
  }
  else
  {
    glDrawTexiOES(0, 0, 0, d_w, d_h);
    //  LOGE("=================== 2 native_gl_render,w is %d,hid is %d,channel:%d", channel,d_w,d_h);
  }

  check_gl_error("glDrawTexiOES");
  if (!thNet_IsConnect(Cfg[channel].NetHandle))
    return 0;
  else if (decParam[channel].encLen == 0)
  {
    return 0;
  }
  else
    return 1;
}
